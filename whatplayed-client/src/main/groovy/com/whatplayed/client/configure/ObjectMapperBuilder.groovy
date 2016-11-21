package com.whatplayed.client.configure

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule

class ObjectMapperBuilder {

    ObjectMapper build() {
        ObjectMapper objectMapper = new ObjectMapper()
        configure(objectMapper)
        return objectMapper
    }

    void configure(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JodaModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

}
