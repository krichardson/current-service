package net.krisr.current.modules

import net.krisr.current.api.Chart
import net.krisr.current.dao.ChartDAO
import net.krisr.current.domain.ArtistEntity
import net.krisr.current.domain.ChartEntity
import net.krisr.current.domain.PlacementEntity
import net.krisr.current.domain.SongEntity
import org.dozer.Mapper
import org.joda.time.LocalDate
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.jsoup.nodes.Element

class ChartModule {

    Mapper beanMapper
    ChartDAO chartDAO
    ArtistModule artistModule
    SongModule songModule

    ChartModule(Mapper beanMapper, ChartDAO chartDAO, ArtistModule artistModule, SongModule songModule) {
        this.beanMapper = beanMapper
        this.chartDAO = chartDAO
        this.artistModule = artistModule
        this.songModule = songModule
    }

    Chart getChart(Long id) {
       ChartEntity chartEntity = chartDAO.findById(id)
        return beanMapper.map(chartEntity, Chart)
    }

    Chart parseUrl(String chartUrl, LocalDate chartDate) {

        Document doc = Jsoup.connect(chartUrl).get()
        ChartEntity chartEntity = parseDocument(doc, chartDate)
        return beanMapper.map(chartEntity, Chart)

    }

    Chart parseHtml(String html, LocalDate chartDate) {

        Document doc = Jsoup.parse(html)
        ChartEntity chartEntity = parseDocument(doc, chartDate)
        return beanMapper.map(chartEntity, Chart)

    }

    private ChartEntity parseDocument(Document doc, LocalDate chartDate) {

        ChartEntity chart = findOrCreateChart(chartDate)

        //If the chart already exists w/ placements, then skip updating
        if (chart.placements) {
            return chart
        }

        Elements chartTableRows = doc.select('table.chartshow tr')

        List<Map> foundSongs = []
        int nextRow = 1
        if (chartTableRows.size() >= 20) {
            ListIterator<Element> iterator = chartTableRows.listIterator()
            while (iterator.hasNext()) {
                Element row = iterator.next()
                Elements cells = row.select('td')
                if (cells.size() >= 5 && tdValue(cells[0]) == nextRow.toString()) {
                    foundSongs << [position: nextRow, artist: tdValue(cells[3]), title: tdValue(cells[4])]
                    nextRow++
                }
            }
        }

        chart.placements = foundSongs.collect {
            ArtistEntity artist = artistModule.findOrCreateArtist(it.artist)
            SongEntity song = songModule.findOrCreateSong(artist, it.title)
            PlacementEntity placement = new PlacementEntity(chart: chart, position: it.position, song: song)
            return placement
        }
        chartDAO.createOrUpdate(chart)

        return chart
    }

    private ChartEntity findOrCreateChart(LocalDate chartDate) {
        ChartEntity chartEntity = chartDAO.findByDate(chartDate)
        if (!chartEntity) {
            chartEntity = new ChartEntity(date: chartDate)
            chartDAO.createOrUpdate(chartEntity)
        }
        return chartEntity
    }



    private static String tdValue(Element element) {
        def childNode = element.childNodes()[0]
        return (childNode instanceof TextNode) ? childNode.text() : null
    }

}
