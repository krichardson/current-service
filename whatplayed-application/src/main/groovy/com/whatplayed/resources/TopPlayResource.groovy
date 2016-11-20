package com.whatplayed.resources

import com.codahale.metrics.annotation.Timed
import com.whatplayed.api.Source
import com.whatplayed.modules.SourceModule
import io.dropwizard.jersey.params.DateTimeParam
import io.dropwizard.jersey.params.IntParam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import com.whatplayed.api.PlaySummary
import com.whatplayed.api.TopPlaysRequest
import com.whatplayed.api.TopPlaysResponse
import com.whatplayed.modules.PlayModule
import org.hibernate.validator.valuehandling.UnwrapValidatedValue
import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/sources/{sourceId}/topplays')
@Produces(MediaType.APPLICATION_JSON)
@Api(value = 'Top Plays')
class TopPlayResource extends AbstractSourceResource {

    private final PlayModule playModule
    private static final Integer MAX_TOP_PLAYS = 100

    TopPlayResource(final PlayModule playModule,
                    final SourceModule sourceModule) {
        super(sourceModule)
        this.playModule = playModule
    }

    @GET
    @Timed
    @ApiOperation(value = 'Get the top plays for a specified time range')
    TopPlaysResponse plays(@PathParam('sourceId') String sourceIdOrAll,
                           @QueryParam('rangeStartTime') @UnwrapValidatedValue @NotNull DateTimeParam rangeStartTime,
                           @QueryParam('rangeEndTime') @UnwrapValidatedValue @NotNull DateTimeParam rangeEndTime,
                           @QueryParam('limit') @DefaultValue('20') IntParam limit,
                           @QueryParam('offset') @DefaultValue('0') IntParam offset) {

        Long sourceId = null
        if (sourceIdOrAll != 'all') {
            try {
                sourceId = Long.parseLong(sourceIdOrAll)
            } catch (NumberFormatException e) {
                throw new WebApplicationException(
                        Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                                .entity('sourceId must be numeric or "all"')
                                .build())
            }
        }

        Source source = sourceId ? getSource(sourceId) : null

        TopPlaysRequest request = new TopPlaysRequest(
                source: source,
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

        List<PlaySummary> topPlays = playModule.getTopPlays(request)
        return new TopPlaysResponse(
                request: request,
                topPlays: topPlays,
        )

    }

}
