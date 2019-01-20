package com.whatplayed.current.service

import com.whatplayed.api.Play
import com.whatplayed.api.PlayRequest
import com.whatplayed.client.PlayApi
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Response
import spock.lang.Specification

class ImportServiceSpec extends Specification {

    private ImportService service
    private PlayApi playApi

    void setup() {
        playApi = Mock()
        service = new ImportService(playApi)
    }

    def 'parse play data from an html document'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 19, 0)
        String html = this.getClass().getResource('/fixtures/playlist.html').text

        String artist1 = 'James Buckley Trio'
        String song1 = 'What\'s Yours'
        String artist2 = 'Gay Beast'
        String song2 = 'Smithereens'

        Call<Play> playCall = GroovyMock()
        Response<Play> playResponse = GroovyMock()

        when: 'Parsing the chart data out of the html'
        service.parseHtml(html, playHour)

        then:
        2 * playApi.recordPlay(1, { PlayRequest p ->
            assert [artist1, artist2].contains(p.artistName)
            assert [song1, song2].contains(p.songTitle)
            assert [52, 56].contains(p.playTime.minuteOfHour)
            return p
        } as PlayRequest) >> playCall
        2 * playCall.execute() >> playResponse
        2 * playResponse.successful >> true
        2 * playResponse.body() >> new Play()
    }

    def 'Return empty list when there is no data for the hour'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 21, 0)
        String html = this.getClass().getResource('/fixtures/playlist_nodata.html').text

        when: 'Parsing the chart data out of the html'
        List<Play> playList = service.parseHtml(html, playHour)

        then:
        assert playList.size() == 0
    }

    def 'Skip plays that do not have an artist'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 19, 0)
        String html = this.getClass().getResource('/fixtures/playlist_empty_artist.html').text

        when: 'Parsing the chart data out of the html'
        List<Play> playList = service.parseHtml(html, playHour)

        then:
        assert playList.size() == 0
    }

    def 'Skip plays that do not have a song title'() {
        setup: 'A known html file'
        DateTime playHour = new DateTime(2011, 01, 16, 19, 0)
        String html = this.getClass().getResource('/fixtures/playlist_empty_title.html').text

        when: 'Parsing the chart data out of the html'
        List<Play> playList = service.parseHtml(html, playHour)

        then:
        assert playList.size() == 0
    }

}
