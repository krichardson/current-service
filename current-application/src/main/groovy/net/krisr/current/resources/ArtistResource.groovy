package net.krisr.current.resources

import com.codahale.metrics.annotation.Timed
import io.dropwizard.jersey.params.IntParam
import io.dropwizard.jersey.params.LongParam
import net.krisr.current.api.Artist
import net.krisr.current.modules.ArtistModule
import org.hibernate.validator.constraints.NotEmpty

import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/artists')
@Produces(MediaType.APPLICATION_JSON)
class ArtistResource {

    ArtistModule artistModule

    ArtistResource(ArtistModule artistModule) {
        this.artistModule = artistModule
    }

    @GET
    @Timed
    List<Artist> listArtists(@QueryParam('limit') @DefaultValue('50') IntParam limit,
                             @QueryParam('offset') @DefaultValue('0') IntParam offset) {
        return artistModule.listArtists(limit.get(), offset.get())
    }

    @GET
    @Timed
    @Path('/{artistId}')
    Artist getArtist(@PathParam('artistId') LongParam artistId) {
        Artist artist = artistModule.findById(artistId.get())
        if (!artist) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }
        return artist
    }

    @GET
    @Timed
    @Path('/search')
    List<Artist> searchArtists(@QueryParam('name') @NotEmpty String name) {
        return [artistModule.findByName(name)]
    }

}
