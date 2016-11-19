package net.krisr.current.modules

import net.krisr.current.api.Artist
import net.krisr.current.api.Chart
import net.krisr.current.api.Placement
import net.krisr.current.api.Song
import net.krisr.current.dao.ChartDAO
import net.krisr.current.dao.PlacementDAO

import org.joda.time.LocalDate
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.jsoup.nodes.Element

class ChartModule {

    private static final int CONNECT_TIMEOUT_MILLIS = 8000
    private final ChartDAO chartDAO
    private final PlacementDAO placementDAO
    private final ArtistModule artistModule
    private final SongModule songModule

    ChartModule(ChartDAO chartDAO,
                PlacementDAO placementDAO,
                ArtistModule artistModule,
                SongModule songModule) {
        this.chartDAO = chartDAO
        this.placementDAO = placementDAO
        this.artistModule = artistModule
        this.songModule = songModule
    }

    List<Chart> listAllCharts() {
        return chartDAO.findAll()
    }

    Chart getChart(Long id) {
        Chart chart = chartDAO.findById(id)
        if (chart) {
            chart.placements = placementDAO.findAllByChartId(chart.id)
        }
       return chart
    }

    Chart getChart(LocalDate chartDate) {
        Chart chart = chartDAO.findByDate(chartDate)
        if (chart) {
            chart.placements = placementDAO.findAllByChartId(chart.id)
        }
        return chart
    }

    Chart parseUrl(String chartUrl, LocalDate chartDate) {
        Connection connection = Jsoup.connect(chartUrl).timeout(CONNECT_TIMEOUT_MILLIS)
        Document doc = connection.get()
        return parseDocument(doc, chartDate)
    }

    Chart parseHtml(String html, LocalDate chartDate) {
        Document doc = Jsoup.parse(html)
        return parseDocument(doc, chartDate)
    }

    private Chart parseDocument(Document doc, LocalDate chartDate) {

        Chart chart = findOrCreateChart(chartDate)

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
            Artist artist = artistModule.findOrCreateArtist(it.artist)
            Song song = songModule.findOrCreateSong(artist, it.title)
            Placement placement = new Placement(position: it.position, song: song)
            placementDAO.create(chart.id, it.position, song.id)
            return placement
        }
        return chart
    }

    private Chart findOrCreateChart(LocalDate chartDate) {
        Chart chart = chartDAO.findByDate(chartDate)
        if (chart) {
            chart.placements = placementDAO.findAllByChartId(chart.id)
        } else {
            chart = new Chart(date: chartDate, placements: [])
            chart.id = chartDAO.create(chartDate)
        }
        return chart
    }

    @SuppressWarnings('Instanceof')
    private static String tdValue(Element element) {
        Node childNode = element.childNodes()[0]
        return (childNode instanceof TextNode) ? childNode.text().trim() : null
    }

}
