package com.whatplayed.current.service

import com.whatplayed.api.Play
import com.whatplayed.api.PlayRequest
import com.whatplayed.client.PlayApi
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Response

@SuppressWarnings('Println')
@SuppressWarnings('PrivateFieldCouldBeFinal')
@Slf4j
class ImportService {


    public static final DateTimeZone SOURCE_TIME_ZONE = DateTimeZone.forID('America/Chicago')

    private static final int CONNECT_TIMEOUT_MILLIS = 8000
    private static final Long SOURCE_ID = 1
    private static final String DATE_PATTERN = 'yyyy-MM-dd'
    private final PlayApi playApi

    ImportService(final PlayApi playApi) {
        this.playApi = playApi
    }

    LocalDateTime findLastImportTime() {
        Call<Play> latestPlayCall = playApi.getLatestPlay(SOURCE_ID)
        Response<Play> latestPlayResponse = latestPlayCall.execute()
        if (latestPlayResponse.successful) {
            return latestPlayResponse.body()?.playTime
        }
        return null
    }

    List<Play> importPlaylist(DateTime startTime) {
        //Don't do more than a month at a time
        DateTime endTime = new DateTime()
        if (startTime.plusMonths(1) < endTime) {
            endTime = startTime.plusMonths(1)
        }

        List<Play> playsImported = []
        DateTime currentHour = startTime
        while (currentHour < endTime) {
            String dataUrl = buildHourUrl(currentHour)
            try {
                playsImported.addAll parseUrl(dataUrl, currentHour)
            } catch (IOException e) {
                println "Unable to fetch data form ${dataUrl}: ${e.message}"
            }
            //For any non-first hour, set to the start of the hour
            currentHour = currentHour
                    .plusHours(1)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0)
        }
        return playsImported
    }

    protected List<Play> parseHtml(String html, DateTime currentHour) {
        Document doc = Jsoup.parse(html)
        return parseDocument(doc, currentHour)
    }

    private static String buildHourUrl(DateTime hour) {
        return 'http://www.thecurrent.org/playlist/' +
                hour.toString(DATE_PATTERN) + '/' + hour.toString('H') +
                '?isajax=1'
    }

    @SuppressWarnings('Instanceof')
    private static String nodeValue(Node node) {
        //Search for the first child node that contains text

        if (node instanceof TextNode) {
            return node.text().trim()
        }

        //Go through each of the children looking for the first TextNode
        for (Node n : node.childNodes()) {
            String val = nodeValue(n)
            if (val) {
                return val
            }
        }

        //All children checked and no text nodes were found
        return null
    }

    private static String elementAttributeValue(Element element, String attributeName) {
        return element.attr(attributeName).trim()
    }

    private List<Play> parseUrl(String url, DateTime currentHour, Boolean retry = true) {
        log.info "Fetching play data from ${url}"
        Document doc
        try {
            Connection connection = Jsoup.connect(url).timeout(CONNECT_TIMEOUT_MILLIS)
            doc = connection.get()
        } catch (IOException e) {
            if (!retry) {
                throw e
            }
            return parseUrl(url, currentHour, false)
        }

        return parseDocument(doc, currentHour)
    }

    private List<Play> parseDocument(Document doc, DateTime currentHourStart) {
        Elements songRows = doc.select('article.song')
        if (songRows.size() == 1 && songRows.html().contains('No playlist data available for this hour.')) {
            return []
        }

        List<Play> plays = []
        ListIterator<Element> iterator = songRows.listIterator()
        DateTimeFormatter formatter = DateTimeFormat.forPattern('yyyy-MM-dd H:mm')
        String defaultDateString = currentHourStart.toString(DATE_PATTERN)
        while (iterator.hasNext()) {
            Element row = iterator.next()

            //Artist
            Element artistElement = row.select('div h5.artist')[0]
            String artistName = nodeValue(artistElement)
            if (!artistName) {
                continue
            }

            //Song Title
            Element songElement = row.select('div h5.title')[0]
            String songTitle = nodeValue(songElement)
            if (!songTitle) {
                continue
            }

            //Date/Time
            Element dateTime = row.select('div.songTime time')[0]
            String dateString = elementAttributeValue(dateTime, 'datetime') ?: defaultDateString
            String timeString = nodeValue(dateTime)

            //The parsed time is 12 hour format, so need to set the correct 24 hour hour
            LocalDateTime playTime
            try {
                playTime = LocalDateTime.parse(dateString + ' ' + timeString, formatter)
                Integer minute = playTime.minuteOfHour
                playTime = playTime
                        .withHourOfDay(currentHourStart.hourOfDay)
                        .withMinuteOfHour(minute)
            } catch (IllegalArgumentException e) {
                log.debug("Unable to parse time: ${e.message}")
                playTime = currentHourStart.toLocalDateTime()
            }

            //Save the stuff
            if (playTime.toDateTime(SOURCE_TIME_ZONE) >= currentHourStart) {
                PlayRequest request = new PlayRequest(artistName: artistName, songTitle: songTitle, playTime: playTime)
                Call<Play> playCall = playApi.recordPlay(SOURCE_ID, request)
                Response<Play> playResponse = playCall.execute()
                if (playResponse.successful) {
                    plays.add(playResponse.body())
                }
            }
        }
        return plays
    }
}
