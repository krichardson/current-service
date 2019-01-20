package com.whatplayed.resources

import com.codahale.metrics.annotation.Timed
import io.dropwizard.jersey.params.IntParam
import io.dropwizard.jersey.params.LongParam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import com.whatplayed.api.Artist
import com.whatplayed.modules.ArtistModule
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
@Api(value = 'Artists')
class ArtistResource {

    ArtistModule artistModule

    ArtistResource(ArtistModule artistModule) {
        this.artistModule = artistModule
    }

    @GET
    @Timed
    @ApiOperation(value = 'Get a list of artists, based on play data')
    List<Artist> listArtists(@QueryParam('limit') @DefaultValue('50') IntParam limit,
                             @QueryParam('offset') @DefaultValue('0') IntParam offset) {
        return artistModule.listArtists(limit.get(), offset.get())
    }

    @GET
    @Timed
    @Path('/{artistId}')
    @ApiOperation(value = 'Get information about an artist')
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
    @ApiOperation(value = 'Search for an artist')
    List<Artist> searchArtists(@QueryParam('name') @NotEmpty String name) {
        return [artistModule.findByName(name)]
    }

}
