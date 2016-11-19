package net.krisr.current.api

import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull

class PlaylistRequest {

    @NotNull
    LocalDateTime rangeStartTime

    @NotNull
    LocalDateTime rangeEndTime

}
