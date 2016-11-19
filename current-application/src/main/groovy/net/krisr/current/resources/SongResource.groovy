package net.krisr.current.resources

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('/artists/{artistId}/songs')
@Produces(MediaType.APPLICATION_JSON)
class SongResource {
}
