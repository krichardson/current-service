package com.whatplayed.api

import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull

class PlaylistRequest {

    @NotNull
    LocalDateTime rangeStartTime

    @NotNull
    LocalDateTime rangeEndTime

}
