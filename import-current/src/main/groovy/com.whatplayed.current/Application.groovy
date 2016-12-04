package com.whatplayed.current

import com.fasterxml.jackson.databind.ObjectMapper
import com.whatplayed.api.Play
import com.whatplayed.client.PlayApi
import com.whatplayed.client.configure.ClientConfiguration
import com.whatplayed.client.configure.ObjectMapperBuilder
import com.whatplayed.client.configure.RetrofitBuilder
import com.whatplayed.current.service.ImportService
import groovy.util.logging.Slf4j
import groovyjarjarcommonscli.MissingArgumentException
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import retrofit2.Retrofit

@Slf4j
class Application {

    static void main(String[] args) {

        if (args.length == 0 || args[0].trim() == '') {
            throw new MissingArgumentException('Must provide the WhatPlayed service url as the first argument')
        }

        //Wait for service to become available
        sleep(20000)

        new Application().run(args[0].trim())
    }

    void run(String whatplayedServiceUrl) {

        log.info 'Starting to run job'

        PlayApi playApi = buildPlayApi(whatplayedServiceUrl)
        ImportService importService = new ImportService(playApi)

        LocalDateTime lastImport = importService.findLastImportTime() ?: EARLIEST_START_DATE
        //Add a second, so we don't try to re-import the last song
        //Also need to use a DateTime so DST is considered
        DateTime importStartTime = lastImport.toDateTime(ImportService.SOURCE_TIME_ZONE).plusSeconds(1)
        log.info "Importing plays since ${importStartTime}"
        List<Play> playsImported = importService.importPlaylist(importStartTime)

        log.info "Imported ${playsImported.size()} plays"
        playsImported.each {
            log.info "Imported: ${it.toString()}"
        }

        log.info('Done running job')
    }

    private static buildPlayApi(final String whatplayedServiceUrl) {
        ClientConfiguration clientConfiguration = new ClientConfiguration(baseUrl: whatplayedServiceUrl)
        ObjectMapper objectMapper = new ObjectMapperBuilder().build()
        Retrofit retrofit = new RetrofitBuilder(clientConfiguration)
                .withObjectMapper(objectMapper)
                .build()
        return retrofit.create(PlayApi)
    }

    private static final LocalDateTime EARLIEST_START_DATE = new LocalDateTime(2011, 1, 1, 0, 0)

}
