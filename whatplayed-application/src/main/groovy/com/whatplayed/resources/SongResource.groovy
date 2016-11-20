package com.whatplayed.resources

import io.swagger.annotations.Api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('/artists/{artistId}/songs')
@Produces(MediaType.APPLICATION_JSON)
@Api(value = 'Songs')
class SongResource {
}
