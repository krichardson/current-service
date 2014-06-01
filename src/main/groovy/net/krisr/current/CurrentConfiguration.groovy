package net.krisr.current

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

import javax.validation.Valid
import javax.validation.constraints.NotNull

class CurrentConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    DataSourceFactory database = new DataSourceFactory()
}
