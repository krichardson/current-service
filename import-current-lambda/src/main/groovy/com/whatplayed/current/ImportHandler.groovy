package com.whatplayed.current

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.whatplayed.api.Play
import com.whatplayed.api.PlayRequest
import com.whatplayed.client.PlayApi
import com.whatplayed.client.configure.ClientConfiguration
import com.whatplayed.client.configure.ObjectMapperBuilder
import com.whatplayed.client.configure.RetrofitBuilder
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
import retrofit2.Retrofit

@SuppressWarnings('Println')
@SuppressWarnings('PrivateFieldCouldBeFinal')
class ImportHandler implements RequestHandler<ImportRequest, ImportResponse> {

    private static final Long SOURCE_ID = 1
    private static final LocalDateTime EARLIEST_START_DATE = new LocalDateTime(2011, 1, 1, 0, 0)
    private static final int CONNECT_TIMEOUT_MILLIS = 8000
    private static final DateTimeZone SOURCE_TIME_ZONE = DateTimeZone.forID('America/Chicago')
    private static final String DATE_PATTERN = 'yyyy-MM-dd'

    private static PlayApi playApi = null

    ImportHandler() {
        String baseUrl = System.getenv('WHATPLAYED_SERVICE_URL')
        ClientConfiguration clientConfiguration = new ClientConfiguration(baseUrl: baseUrl)
        ObjectMapper objectMapper = new ObjectMapperBuilder().build()
        Retrofit retrofit = new RetrofitBuilder(clientConfiguration).withObjectMapper(objectMapper).build()
        playApi = retrofit.create(PlayApi)
    }

    ImportHandler(final PlayApi playApi) {
        this.playApi = playApi
    }

    @Override
    ImportResponse handleRequest(ImportRequest input, Context context) {

        //Find the last import
        LocalDateTime lastImport = findLastImportTime() ?: EARLIEST_START_DATE

        //Add a second, so we don't try to re-import the last song
        //Also need to use a DateTime so DST is considered
        DateTime importStartTime = lastImport.toDateTime(SOURCE_TIME_ZONE).plusSeconds(1)
        List<Play> playsImported = importPlaylist(importStartTime)
        return new ImportResponse(playsImported: playsImported)

    }

    static List<Play> importPlaylist(DateTime startTime) {
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
                println "Unable to fetch data form ${dataUrl}"
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

    static List<Play> parseUrl(String url, DateTime currentHour, Boolean retry = true) {
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

    static List<Play> parseHtml(String html, DateTime currentHour) {
        Document doc = Jsoup.parse(html)
        return parseDocument(doc, currentHour)
    }

    private static List<Play> parseDocument(Document doc, DateTime currentHourStart) {
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

    private LocalDateTime findLastImportTime() {
        Call<Play> latestPlayCall = playApi.getLatestPlay(SOURCE_ID)
        Response<Play> latestPlayResponse = latestPlayCall.execute()
        if (latestPlayResponse.successful) {
            return latestPlayResponse.body()?.playTime
        }
        return null
    }
}
