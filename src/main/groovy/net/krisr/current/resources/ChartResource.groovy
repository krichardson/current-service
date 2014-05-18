package net.krisr.current.resources

import com.yammer.dropwizard.hibernate.UnitOfWork
import com.yammer.dropwizard.jersey.params.DateTimeParam
import com.yammer.dropwizard.jersey.params.IntParam
import com.yammer.dropwizard.jersey.params.LongParam
import com.yammer.metrics.annotation.Timed
import net.krisr.current.api.Chart
import net.krisr.current.domain.ChartEntity
import net.krisr.current.modules.ChartModule

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path('/chart')
@Produces(MediaType.APPLICATION_JSON)
class ChartResource {

    ChartModule chartModule

    ChartResource(ChartModule chartModule) {
        this.chartModule = chartModule
    }

    @GET
    @Timed
    @UnitOfWork(transactional = false)
    Chart parseChart(@QueryParam('chartUrl') String chartUrl,
                     @QueryParam('chartDate') DateTimeParam chartDate) {
        return chartModule.parseUrl(chartUrl, chartDate.get().toLocalDate())
    }

    @GET
    @Timed
    @UnitOfWork(transactional = false)
    @Path('/{chartId}')
    Chart getChart(@PathParam('chartId') LongParam chartId) {
        return chartModule.getChart(chartId.get())
    }

}
