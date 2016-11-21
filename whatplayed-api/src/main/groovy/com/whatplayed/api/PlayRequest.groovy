package com.whatplayed.api

import org.hibernate.validator.constraints.NotEmpty
import org.joda.time.LocalDateTime

import javax.validation.constraints.NotNull

class PlayRequest {

    @NotNull
    LocalDateTime playTime

    @NotEmpty
    String artistName

    @NotEmpty
    String songTitle

}
