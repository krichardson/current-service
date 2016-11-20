package com.whatplayed.api

import org.hibernate.validator.constraints.NotBlank
import org.joda.time.LocalDate

import javax.validation.constraints.NotNull

class ChartImportRequest {

    @NotBlank
    String chartUrl

    @NotNull
    LocalDate chartDate

}
