package net.krisr.current.resources

import com.codahale.metrics.annotation.Timed
import io.dropwizard.jersey.params.DateTimeParam
import net.krisr.current.api.Play
import net.krisr.current.client.PlaylistRequest
import net.krisr.current.client.PlaylistResponse
import net.krisr.current.modules.PlaylistModule
import org.joda.time.LocalDateTime
import org.joda.time.Period

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/playlist')
@Produces(MediaType.APPLICATION_JSON)
class PlaylistResource {

    private static final Period MAX_PLAYLIST_RANGE = Period.days(1)
    private final PlaylistModule playlistModule

    PlaylistResource(PlaylistModule playlistModule) {
        this.playlistModule = playlistModule
    }

    @GET
    @Timed
    PlaylistResponse plays(@QueryParam('rangeStartTime') DateTimeParam rangeStartTime,
                           @QueryParam('rangeEndTime') DateTimeParam rangeEndTime) {
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
        PlaylistRequest playlistRequest = new PlaylistRequest(rangeStartTime: start, rangeEndTime: end)
        List<Play> playList = playlistModule.getPlays(playlistRequest)
        return new PlaylistResponse(
                request: playlistRequest,
                playCount: playList.size(),
                playList: playList)
    }

}
