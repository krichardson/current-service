package net.krisr.current.modules

import net.krisr.current.api.Chart
import net.krisr.current.dao.ChartDAO
import net.krisr.current.domain.ArtistEntity
import net.krisr.current.domain.ChartEntity
import net.krisr.current.domain.SongEntity
import org.dozer.DozerBeanMapper
import org.dozer.Mapper
import org.joda.time.LocalDate
import spock.lang.Specification

class ChartModuleSpec extends Specification {

    ChartModule chartModule
    Mapper beanMapper
    ChartDAO chartDAO
    ArtistModule artistModule
    SongModule songModule

    def setup() {
        beanMapper = new DozerBeanMapper(['dozer.xml'])
        chartDAO = Mock()
        artistModule = Mock()
        songModule = Mock()
        chartModule = new ChartModule(beanMapper, chartDAO, artistModule, songModule)
    }

    def 'parse some placements out of chart html'() {

        setup: 'A known html file'
        LocalDate chartDate = new LocalDate(2014, 5, 14)
        String html = this.getClass().getResource('/fixtures/chart.html').text

        when: 'Parsing the chart data out of the html'
        Chart chart = chartModule.parseHtml(html, chartDate)

        then: 'Chart is created'
        (1..20).each { int id ->
            artistModule.findOrCreateArtist(_ as String) >> {
                new ArtistEntity(id: id, name: it)
            }
            songModule.findOrCreateSong(_ as ArtistEntity, _ as String) >> { ArtistEntity artist, String title ->
                new SongEntity(id: id, artist: artist, title: title)
            }
        }
        2 * chartDAO.createOrUpdate(_ as ChartEntity)

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
