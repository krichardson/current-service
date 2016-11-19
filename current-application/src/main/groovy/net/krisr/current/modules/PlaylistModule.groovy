package net.krisr.current.modules

import groovy.util.logging.Slf4j
import net.krisr.current.api.Artist
import net.krisr.current.api.Play
import net.krisr.current.api.PlaySummary
import net.krisr.current.api.PlaylistRequest
import net.krisr.current.api.Song
import net.krisr.current.api.TopPlaysRequest
import net.krisr.current.dao.PlayDAO
import net.krisr.current.dao.PlaySummaryDAO
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
class PlaylistModule {

    private static final LocalDateTime EARLIEST_START_DATE = new LocalDateTime(2011, 1, 1, 0, 0)
    private static final int CONNECT_TIMEOUT_MILLIS = 8000
    private static final DateTimeZone SOURCE_TIME_ZONE = DateTimeZone.forID('America/Chicago')
    private final PlayDAO playDAO
    private final PlaySummaryDAO playSummaryDAO
    private final ArtistModule artistModule
    private final SongModule songModule

    PlaylistModule(PlayDAO playDAO, PlaySummaryDAO playSummaryDAO, ArtistModule artistModule, SongModule songModule) {
        this.playDAO = playDAO
        this.playSummaryDAO = playSummaryDAO
        this.artistModule = artistModule
        this.songModule = songModule
    }

    List<Play> getPlays(PlaylistRequest request) {
        return playDAO.findPlaysBetween(request.rangeStartTime, request.rangeEndTime)
    }

    List<PlaySummary> getTopPlays(TopPlaysRequest request) {
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
        Connection connection
        try {
            connection = Jsoup.connect(url).timeout(CONNECT_TIMEOUT_MILLIS)
        } catch (Exception e) {
            if (!retry) {
                throw e
            }
            log.info("Error fetching document: ${e.message}. Will retry", e)
            return parseUrl(url, currentHour, false)
        }
        Document doc = connection.get()
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

        List<Play> parsedPlays = []
        ListIterator<Element> iterator = songRows.listIterator()
        DateTimeFormatter formatter = DateTimeFormat.forPattern('yyyy-MM-dd H:mm')
        String defaultDateString = currentHourStart.toString('yyyy-MM-dd')
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
                play.id = playDAO.create(playTime, song.id)
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
                hour.toString('yyyy-MM-dd') + '/' + hour.toString('H') +
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
