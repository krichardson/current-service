package net.krisr.current.client

import org.joda.time.LocalDateTime

class TopPlaysRequest {

    LocalDateTime rangeStartTime
    LocalDateTime rangeEndTime
    Integer limit
    Integer offset

}
