package net.krisr.current.modules

import groovy.util.logging.Slf4j
import net.krisr.current.api.Artist
import net.krisr.current.api.Play
import net.krisr.current.api.Song
import net.krisr.current.dao.PlayDAO

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements

@Slf4j
class PlaylistModule {

    private static final LocalDateTime EARLIEST_START_DATE = new LocalDateTime(2011, 1, 1, 0, 0)
    private static final int CONNECT_TIMEOUT_MILLIS = 8000
    private final PlayDAO playDAO
    private final ArtistModule artistModule
    private final SongModule songModule

    PlaylistModule(PlayDAO playDAO, ArtistModule artistModule, SongModule songModule) {
        this.playDAO = playDAO
        this.artistModule = artistModule
        this.songModule = songModule
    }

    List<Play> importPlaylist() {
        //Find the last import
        LocalDateTime lastImport = findLastImportTime() ?: EARLIEST_START_DATE
        //Add a second, so we don't try to re-import the last song
        return importPlaylist(lastImport.plusSeconds(1))
    }

    List<Play> importPlaylist(LocalDateTime startTime) {
        //Don't do more than a month at a time
        LocalDateTime endTime = new LocalDateTime()
        if (startTime.plusMonths(1) < endTime) {
            endTime = startTime.plusMonths(1)
        }

        List<Play> playsImported = []
        LocalDateTime currentHour = startTime
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

    List<Play> parseUrl(String url, LocalDateTime currentHour) {
        Connection connection = Jsoup.connect(url).timeout(CONNECT_TIMEOUT_MILLIS)
        Document doc = connection.get()
        return parseDocument(doc, currentHour)
    }

    List<Play> parseHtml(String html, LocalDateTime currentHour) {
        Document doc = Jsoup.parse(html)
        return parseDocument(doc, currentHour)
    }

    private List<Play> parseDocument(Document doc, LocalDateTime currentHourStart) {
        Elements songRows = doc.select('article.song')
        if (songRows.size() == 1 && songRows.html().contains('No playlist data available for this hour.')) {
            log.info("There are no songs available ${currentHourStart}.")
            return []
        }

        List<Play> parsedPlays = []
        ListIterator<Element> iterator = songRows.listIterator()
        DateTimeFormatter formatter = DateTimeFormat.forPattern('yyyy-MM-dd H:mm')
        while (iterator.hasNext()) {
            Element row = iterator.next()

            //Artist
            Element artistElement = row.select('div h5.artist')[0]
            String artistName = elementValue(artistElement)

            //Song Title
            Element songElement = row.select('div h5.title')[0]
            String songTitle = elementValue(songElement)

            //Date/Time
            Element dateTime = row.select('div.songTime time')[0]
            String dateString = elementAttributeValue(dateTime, 'datetime')
            String timeString = elementValue(dateTime)

            //The parsed time is 12 hour format, so need to set the correct 24 hour hour
            LocalDateTime playTime = LocalDateTime.parse(dateString + ' ' + timeString, formatter)
            playTime = playTime.withHourOfDay(currentHourStart.hourOfDay)

            //Save the stuff
            if (playTime >= currentHourStart) {
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

    private String buildHourUrl(LocalDateTime hour) {
        return 'http://www.thecurrent.org/playlist/' +
                hour.toString('yyyy-MM-dd') + '/' + hour.toString('H') +
                '?isajax=1'
    }

    private LocalDateTime findLastImportTime() {
        return playDAO.findLatestPlayTime()
    }

    private static String elementValue(Element element) {
        def childNode = element.childNodes()[0]
        return (childNode instanceof TextNode) ? childNode.text().trim() : null
    }

    private static String elementAttributeValue(Element element, String attributeName) {
        return element.attr(attributeName).trim()
    }

}
