package net.krisr.current.modules

import net.krisr.current.domain.ChartEntity
import org.joda.time.LocalDate
import spock.lang.Specification

class ChartModuleSpec extends Specification {

    ChartModule chartModule

    def setup() {
        chartModule = new ChartModule()
    }

    def 'parse some placements out of chart html'() {

        setup: 'A known html file'
        LocalDate chartDate = new LocalDate(2014, 5, 14)
        String html = this.getClass().getResource('/fixtures/chart.html').text

        when: 'Parsing the chart data out of the html'
        ChartEntity chart = chartModule.parseHtml(html, chartDate)

        then: 'Chart is created'
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
