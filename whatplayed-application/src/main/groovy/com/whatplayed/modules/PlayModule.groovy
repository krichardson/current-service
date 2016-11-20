package com.whatplayed.modules

import com.whatplayed.api.Source
import groovy.util.logging.Slf4j
import com.whatplayed.api.Artist
import com.whatplayed.api.Play
import com.whatplayed.api.PlaySummary
import com.whatplayed.api.PlaylistRequest
import com.whatplayed.api.Song
import com.whatplayed.api.TopPlaysRequest
import com.whatplayed.dao.PlayDAO
import com.whatplayed.dao.PlaySummaryDAO
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

@Slf4j
class PlayModule {

    private static final LocalDateTime EARLIEST_START_DATE = new LocalDateTime(2011, 1, 1, 0, 0)
    private static final int CONNECT_TIMEOUT_MILLIS = 8000
    private static final DateTimeZone SOURCE_TIME_ZONE = DateTimeZone.forID('America/Chicago')
    private static final String DATE_PATTERN = 'yyyy-MM-dd'
    private final PlayDAO playDAO
    private final PlaySummaryDAO playSummaryDAO
    private final SourceModule sourceModule
    private final ArtistModule artistModule
    private final SongModule songModule

    PlayModule(PlayDAO playDAO,
               PlaySummaryDAO playSummaryDAO,
               SourceModule sourceModule,
               ArtistModule artistModule,
               SongModule songModule) {
        this.playDAO = playDAO
        this.playSummaryDAO = playSummaryDAO
        this.sourceModule = sourceModule
        this.artistModule = artistModule
        this.songModule = songModule
    }

    List<Play> getPlays(PlaylistRequest request) {
        return playDAO.findPlaysBetween(request.source.id, request.rangeStartTime, request.rangeEndTime)
    }

    List<PlaySummary> getTopPlays(TopPlaysRequest request) {
        if (request.source) {
            return playSummaryDAO.findTopPlaysBetween(request.source.id, request.rangeStartTime, request.rangeEndTime,
                    request.limit, request.offset)
        }
        return playSummaryDAO.findTopPlaysBetween(request.rangeStartTime, request.rangeEndTime,
                request.limit, request.offset)
    }

    List<Play> importPlaylist() {
        //Find the last import
        LocalDateTime lastImport = findLastImportTime() ?: EARLIEST_START_DATE

        //Add a second, so we don't try to re-import the last song
        //Also need to use a DateTime so DST is considered
        DateTime importStartTime = lastImport.toDateTime(SOURCE_TIME_ZONE).plusSeconds(1)
        return importPlaylist(importStartTime)
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
            log.info("Parsing play data for ${dataUrl}")
            try {
                playsImported.addAll parseUrl(dataUrl, currentHour)
            } catch (IOException e) {
                log.warn("Unable to get songs for ${dataUrl}: ${e.message}")
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

    List<Play> parseUrl(String url, DateTime currentHour, Boolean retry = true) {
        Document doc
        try {
            Connection connection = Jsoup.connect(url).timeout(CONNECT_TIMEOUT_MILLIS)
            doc = connection.get()
        } catch (IOException e) {
            if (!retry) {
                throw e
            }
            log.info("Error fetching document: ${e.message}. Will retry", e)
            return parseUrl(url, currentHour, false)
        }

        return parseDocument(doc, currentHour)

    }

    List<Play> parseHtml(String html, DateTime currentHour) {
        Document doc = Jsoup.parse(html)
        return parseDocument(doc, currentHour)
    }

    private List<Play> parseDocument(Document doc, DateTime currentHourStart) {
        Elements songRows = doc.select('article.song')
        if (songRows.size() == 1 && songRows.html().contains('No playlist data available for this hour.')) {
            log.info("There are no songs available ${currentHourStart}.")
            return []
        }

        Source source = sourceModule.findSourceByName('The Current')

        List<Play> parsedPlays = []
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
                log.warn("Unable to parse date ${dateString} + time ${timeString}. Defaulting to the hour", e)
                playTime = currentHourStart.toLocalDateTime()
            }

            //Save the stuff
            if (playTime.toDateTime(SOURCE_TIME_ZONE) >= currentHourStart) {
                Song song = getOrCreateSong(artistName, songTitle)
                Play play = new Play(song: song, playTime: playTime)
                play.id = playDAO.create(playTime, song.id, source.id)
                parsedPlays << play
            }
        }
        return parsedPlays
    }

    private Song getOrCreateSong(String artistName, String songTitle) {
        Artist artist = artistModule.findOrCreateArtist(artistName)
        Song song = songModule.findOrCreateSong(artist, songTitle)
        return song
    }

    private String buildHourUrl(DateTime hour) {
        return 'http://www.thecurrent.org/playlist/' +
                hour.toString(DATE_PATTERN) + '/' + hour.toString('H') +
                '?isajax=1'
    }

    private LocalDateTime findLastImportTime() {
        return playDAO.findLatestPlayTime()
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

}
