package com.whatplayed.api

import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull

class TopPlaysRequest {

    @NotNull
    Long sourceId

    @NotNull
    LocalDateTime rangeStartTime

    @NotNull
    LocalDateTime rangeEndTime

    Integer limit

    Integer offset

}
