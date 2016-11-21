package com.whatplayed.resources

import com.codahale.metrics.annotation.Timed
import com.whatplayed.api.PlayRequest
import com.whatplayed.api.Source
import com.whatplayed.modules.SourceModule
import io.dropwizard.jersey.params.DateTimeParam
import io.dropwizard.jersey.params.LongParam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import com.whatplayed.api.Play
import com.whatplayed.api.PlaylistRequest
import com.whatplayed.api.PlaylistResponse
import com.whatplayed.modules.PlayModule
import org.hibernate.validator.valuehandling.UnwrapValidatedValue
import org.joda.time.LocalDateTime
import org.joda.time.Period

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/source/{sourceId}/plays')
@Produces(MediaType.APPLICATION_JSON)
@Api(value = 'Plays')
class PlayResource extends AbstractSourceResource {

    private static final Period MAX_PLAYLIST_RANGE = Period.days(1)
    private final PlayModule playModule

    PlayResource(PlayModule playModule,
                 SourceModule sourceModule) {
        super(sourceModule)
        this.playModule = playModule
    }

    @GET
    @Timed
    @ApiOperation(value = 'Get the plays for a specified time period')
    PlaylistResponse plays(@PathParam('sourceId') LongParam sourceId,
                           @QueryParam('rangeStartTime') @UnwrapValidatedValue @NotNull DateTimeParam rangeStartTime,
                           @QueryParam('rangeEndTime') @UnwrapValidatedValue @NotNull DateTimeParam rangeEndTime) {
        LocalDateTime start = rangeStartTime ?
                new LocalDateTime(rangeStartTime.get()) : new LocalDateTime()
        LocalDateTime end = rangeEndTime ?
                new LocalDateTime(rangeEndTime.get()) : start + MAX_PLAYLIST_RANGE

        if (start >= end) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity('rangeEndTime must be later than rangeStartTime')
                            .build()
            )
        }

        if ((start + MAX_PLAYLIST_RANGE) < end) {
            //
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("The requested date range cannot be greater than ${MAX_PLAYLIST_RANGE}".toString())
                            .build()
            )
        }

        Source source = getSource(sourceId.get())
        PlaylistRequest playlistRequest = new PlaylistRequest(source: source, rangeStartTime: start, rangeEndTime: end)
        List<Play> playList = playModule.getPlays(playlistRequest)
        return new PlaylistResponse(
                request: playlistRequest,
                playCount: playList.size(),
                playList: playList,
        )
    }

    @GET
    @Path('/latest')
    @Timed
    @ApiOperation(value = 'Get the most recent play for the source')
    Play getLatestPlay(@PathParam('sourceId') LongParam sourceId) {
        Source source = getSource(sourceId.get())
        return playModule.getMostRecentPlay(source)
    }

    @POST
    @Timed
    @ApiOperation(value = 'Record a play for the source')
    Play recordPlay(@PathParam('sourceId') LongParam sourceId,
                    @Valid PlayRequest playRequest) {

        Source source = getSource(sourceId.get())
        playModule.recordPlay(source, playRequest)

    }

}
