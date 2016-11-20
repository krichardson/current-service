package com.whatplayed.modules

import com.whatplayed.api.Artist
import com.whatplayed.api.Play
import com.whatplayed.api.Song
import com.whatplayed.dao.PlayDAO
import com.whatplayed.dao.PlaySummaryDAO
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import spock.lang.Specification

class PlaylistModuleSpec extends Specification {

    PlayDAO playDAO
    PlaySummaryDAO playSummaryDAO
    ArtistModule artistModule
    SongModule songModule
    PlaylistModule playlistModule

    def setup() {
        playDAO = Mock()
        playSummaryDAO = Mock()
        artistModule = Mock()
        songModule = Mock()
        playlistModule = new PlaylistModule(playDAO, playSummaryDAO, artistModule, songModule)
    }

    def 'parse play data from an html document'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 19, 0)
        String html = this.getClass().getResource('/fixtures/playlist.html').text

        Artist artist1 = new Artist(id: 1, name: 'James Buckley Trio')
        Song song1 = new Song(id: 1, artist: artist1, title: 'What\'s Yours')
        Artist artist2 = new Artist(id: 2, name: 'Gay Beast')
        Song song2 = new Song(id: 2, artist: artist2, title: 'Smithereens')

        when: 'Parsing the chart data out of the html'
        List<Play> playList = playlistModule.parseHtml(html, playHour)

        then:
        1 * artistModule.findOrCreateArtist(artist1.name) >> artist1
        1 * songModule.findOrCreateSong(artist1, song1.title) >> song1
        1 * playDAO.create(_ as LocalDateTime, _ as Long)

        1 * artistModule.findOrCreateArtist(artist2.name) >> artist2
        1 * songModule.findOrCreateSong(artist2, song2.title) >> song2
        1 * playDAO.create(_ as LocalDateTime, _ as Long)

        assert playList.size() == 2
        assert playList[0].playTime == playHour.withMinuteOfHour(56).toLocalDateTime()
        assert playList[0].song == song1
        assert playList[1].playTime == playHour.withMinuteOfHour(52).toLocalDateTime()
        assert playList[1].song == song2
    }

    def 'Return empty list when there is no data for the hour'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 21, 0)
        String html = this.getClass().getResource('/fixtures/playlist_nodata.html').text

        when: 'Parsing the chart data out of the html'
        List<Play> playList = playlistModule.parseHtml(html, playHour)

        then:
        assert playList.size() == 0
    }

    def 'Skip plays that do not have an artist'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 19, 0)
        String html = this.getClass().getResource('/fixtures/playlist_empty_artist.html').text

        when: 'Parsing the chart data out of the html'
        List<Play> playList = playlistModule.parseHtml(html, playHour)

        then:
        assert playList.size() == 0
    }

    def 'Skip plays that do not have a song title'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 19, 0)
        String html = this.getClass().getResource('/fixtures/playlist_empty_title.html').text

        when: 'Parsing the chart data out of the html'
        List<Play> playList = playlistModule.parseHtml(html, playHour)

        then:
        assert playList.size() == 0
    }

}
