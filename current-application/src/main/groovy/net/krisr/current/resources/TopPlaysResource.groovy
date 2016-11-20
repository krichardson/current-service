package net.krisr.current.resources

import com.codahale.metrics.annotation.Timed
import io.dropwizard.jersey.params.DateTimeParam
import io.dropwizard.jersey.params.IntParam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import net.krisr.current.api.PlaySummary
import net.krisr.current.api.TopPlaysRequest
import net.krisr.current.api.TopPlaysResponse
import net.krisr.current.modules.PlaylistModule
import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/topplays')
@Produces(MediaType.APPLICATION_JSON)
@Api(value = 'Top Plays')
class TopPlaysResource {

    private final PlaylistModule playlistModule
    private static final Integer MAX_TOP_PLAYS = 100

    TopPlaysResource(PlaylistModule playlistModule) {
        this.playlistModule = playlistModule
    }

    @GET
    @Timed
    @ApiOperation(value = 'Get the top plays for a specified time range')
    TopPlaysResponse plays(@QueryParam('rangeStartTime') DateTimeParam rangeStartTime,
                           @QueryParam('rangeEndTime') DateTimeParam rangeEndTime,
                           @QueryParam('limit') @DefaultValue('20') IntParam limit,
                           @QueryParam('offset') @DefaultValue('0') IntParam offset) {

        TopPlaysRequest request = new TopPlaysRequest(
                rangeStartTime: new LocalDateTime(rangeStartTime.get()),
                rangeEndTime: new LocalDateTime(rangeEndTime.get()),
                limit: limit.get(),
                offset: offset.get(),
        )


        if (request.rangeStartTime > request.rangeEndTime) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity('rangeEndTime must be later than rangeStartTime')
                            .build()
            )
        }

        if (request.limit > MAX_TOP_PLAYS) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("You cannot request more than ${MAX_TOP_PLAYS} plays at a time".toString())
                            .build()
            )
        }

        if (request.offset < 0) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity('Offset must be an integer greater than or equal to 0')
                            .build()
            )
        }

        List<PlaySummary> topPlays = playlistModule.getTopPlays(request)
        return new TopPlaysResponse(
                request: request,
                topPlays: topPlays,
        )

    }

}
