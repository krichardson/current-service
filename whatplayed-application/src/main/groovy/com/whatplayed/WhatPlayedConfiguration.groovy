package com.whatplayed

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

class WhatPlayedConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    DataSourceFactory database = new DataSourceFactory()

    @Valid
    @NotNull
    @JsonProperty('swagger')
    private SwaggerBundleConfiguration swagger

    SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swagger
    }

}
