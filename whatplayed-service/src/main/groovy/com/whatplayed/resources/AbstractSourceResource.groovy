package com.whatplayed.resources

import com.whatplayed.api.Source
import com.whatplayed.modules.SourceModule

import javax.ws.rs.WebApplicationException

class AbstractSourceResource {

    protected final SourceModule sourceModule

    AbstractSourceResource(final SourceModule sourceModule) {
        this.sourceModule = sourceModule
    }

    Source getSource(Long id) {
        Source source = sourceModule.getSource(id)
        if (!source) {
            throw new WebApplicationException('Source not found', HttpURLConnection.HTTP_NOT_FOUND)
        }
        return source
    }

}
