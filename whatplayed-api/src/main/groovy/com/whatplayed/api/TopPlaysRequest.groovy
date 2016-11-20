package com.whatplayed.api

import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull

class TopPlaysRequest {

    Source source

    @NotNull
    LocalDateTime rangeStartTime

    @NotNull
    LocalDateTime rangeEndTime

    Integer limit

    Integer offset

}
