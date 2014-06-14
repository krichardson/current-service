package net.krisr.current.modules

import net.krisr.current.api.Artist
import net.krisr.current.api.Chart
import net.krisr.current.api.Placement
import net.krisr.current.api.Song
import net.krisr.current.dao.ChartDAO
import net.krisr.current.dao.PlacementDAO
import org.joda.time.LocalDate
import spock.lang.Specification

class ChartModuleSpec extends Specification {

    ChartModule chartModule
    ChartDAO chartDAO
    PlacementDAO placementDAO
    ArtistModule artistModule
    SongModule songModule

    def setup() {
        chartDAO = Mock()
        placementDAO = Mock()
        artistModule = Mock()
        songModule = Mock()
        chartModule = new ChartModule(chartDAO, placementDAO, artistModule, songModule)
    }

    def 'parse some placements out of chart html'() {

        setup: 'A known html file'
        LocalDate chartDate = new LocalDate(2014, 5, 14)
        String html = this.getClass().getResource('/fixtures/chart.html').text
        Long expectedNewChartId = 1

        when: 'Parsing the chart data out of the html'
        Chart chart = chartModule.parseHtml(html, chartDate)

        then: 'Chart is created'
        1 * chartDAO.findByDate(chartDate) >> null
        1 * chartDAO.create(chartDate) >> expectedNewChartId
        20.times { Integer idx ->
            Long newEntityId = idx.toLong() + 1
            Integer position = idx + 1
            1 * artistModule.findOrCreateArtist(_) >> { String artistName ->
                return new Artist(id: newEntityId, name: artistName)
            }
            1 * songModule.findOrCreateSong(_ as Artist, _ as String) >> { Artist artist, String title ->
                return new Song(id: newEntityId, artist: artist, title: title)
            }
            1 * placementDAO.create(expectedNewChartId, position, newEntityId)
        }
        0 * _

        assert chart.date == chartDate

        and: 'There are 20 placements representing spots 1-20'
        assert chart.placements.size() == 20
        (0..19).each { int i ->
            assert chart.placements[i].position == (i + 1)
        }

        and: 'The first song is as expected'
        assert chart.placements[0].song.title == 'Forever'
        assert chart.placements[0].song.artist.name == 'HAIM'

        and: 'The last song is as expected'
        assert chart.placements[19].song.title == 'Algiers'
        assert chart.placements[19].song.artist.name == 'Afghan Whigs'

    }

}
