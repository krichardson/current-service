package com.whatplayed.current

import com.fasterxml.jackson.databind.ObjectMapper
import com.whatplayed.api.Play
import com.whatplayed.client.PlayApi
import com.whatplayed.client.configure.ClientConfiguration
import com.whatplayed.client.configure.ObjectMapperBuilder
import com.whatplayed.client.configure.RetrofitBuilder
import com.whatplayed.current.service.ImportService
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import retrofit2.Retrofit

@Slf4j
class ImportCommand implements Runnable {

    private static final LocalDateTime EARLIEST_START_DATE = new LocalDateTime(2011, 1, 1, 0, 0)

    private final String whatplayedServiceUrl

    ImportCommand(String whatplayedServiceUrl) {
        this.whatplayedServiceUrl = whatplayedServiceUrl
    }

    @Override
    void run() {
        log.info 'Starting to run job'

        try {
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
        } catch (IOException e) {
            log.warn "Unable to complete import run: ${e.message}", e
        }
    }

    private static PlayApi buildPlayApi(final String whatplayedServiceUrl) {
        ClientConfiguration clientConfiguration = new ClientConfiguration(baseUrl: whatplayedServiceUrl)
        ObjectMapper objectMapper = new ObjectMapperBuilder().build()
        Retrofit retrofit = new RetrofitBuilder(clientConfiguration)
                .withObjectMapper(objectMapper)
                .build()
        return retrofit.create(PlayApi)
    }


}
