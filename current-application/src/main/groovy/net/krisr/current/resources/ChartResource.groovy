package net.krisr.current.resources

import io.dropwizard.jersey.params.LongParam
import com.codahale.metrics.annotation.Timed
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import net.krisr.current.api.Chart
import net.krisr.current.api.ChartImportRequest
import net.krisr.current.modules.ChartModule
import org.joda.time.LocalDate

import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/charts')
@Produces(MediaType.APPLICATION_JSON)
@Api(value = 'Charts')
class ChartResource {

    ChartModule chartModule

    ChartResource(ChartModule chartModule) {
        this.chartModule = chartModule
    }

    @GET
    @Timed
    @ApiOperation(value = 'Get a list of charts')
    List<Chart> getCharts() {
        return chartModule.listAllCharts()
    }

    @POST
    @Timed
    @ApiOperation(value = 'Import data for a chart')
    Chart importChart(@Valid ChartImportRequest request) {
        String chartUrl = request.chartUrl
        LocalDate chartDate = request.chartDate
        Chart chart = chartModule.parseUrl(chartUrl, chartDate)
        if (!chart) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }
        return chart
    }

    @GET
    @Timed
    @Path('/{chartId}')
    @ApiOperation('Fetch a chart')
    Chart getChart(@PathParam('chartId') LongParam chartId) {
        Chart chart = chartModule.getChart(chartId.get())
        if (!chart) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }
        return chart
    }

}
