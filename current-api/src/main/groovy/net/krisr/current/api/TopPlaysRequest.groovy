package net.krisr.current.api

import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull

class TopPlaysRequest {

    @NotNull
    LocalDateTime rangeStartTime

    @NotNull
    LocalDateTime rangeEndTime

    Integer limit

    Integer offset

}