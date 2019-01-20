package com.whatplayed.resources

import com.codahale.metrics.annotation.Timed
import com.whatplayed.api.Source
import com.whatplayed.modules.SourceModule
import io.dropwizard.jersey.params.IntParam
import io.dropwizard.jersey.params.LongParam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path('/sources')
@Produces(MediaType.APPLICATION_JSON)
@Api(value = 'Sources')
class SourceResource extends AbstractSourceResource {

    SourceResource(final SourceModule sourceModule) {
        super(sourceModule)
    }

    @GET
    @Timed
    @ApiOperation(value = 'Get a list of sources')
    List<Source> listSources(@QueryParam('limit') @DefaultValue('50') IntParam limit,
                             @QueryParam('offset') @DefaultValue('0') IntParam offset) {
        return sourceModule.listSources(limit.get(), offset.get())
    }

    @GET
    @Timed
    @Path('/{sourceId}')
    @ApiOperation(value = 'Get a specific source')
    Source getSource(@PathParam('sourceId') LongParam sourceId) {
        return getSource(sourceId.get())
    }

}
