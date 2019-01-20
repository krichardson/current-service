package com.whatplayed.client.configure

import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class RetrofitBuilder {

    private final ClientConfiguration clientConfiguration
    private ObjectMapper objectMapper

    RetrofitBuilder(final ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration
    }

    RetrofitBuilder withObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
        return this
    }

    Retrofit build() {
        ObjectMapper om = this.objectMapper ?: new ObjectMapperBuilder().build()
        JacksonConverterFactory converterFactory = JacksonConverterFactory.create(om)
        new Retrofit.Builder()
                .baseUrl(clientConfiguration.baseUrl)
                .addConverterFactory(converterFactory)
                .build()
    }

}
