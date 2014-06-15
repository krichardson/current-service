package net.krisr.current.resources

import io.dropwizard.jersey.params.DateTimeParam
import net.krisr.current.client.PlaylistRequest
import net.krisr.current.modules.PlaylistModule
import org.joda.time.Interval
import org.joda.time.LocalDateTime
import spock.lang.Specification

import javax.ws.rs.WebApplicationException

class PlaylistResourceSpec extends Specification {

    PlaylistResource resource
    PlaylistModule playlistModule

    void setup() {
        playlistModule = Mock()
        resource = new PlaylistResource(playlistModule)
    }

    def "making a request without date ranges defaults to last 24 hours"() {

        setup:
        //kinda hacky, consider using a fixed time for testing?
        LocalDateTime now = new LocalDateTime()
        Interval acceptableStartRange = new Interval(
                now.minusMillis(1000).toDateTime(),
                now.plusMillis(1000).toDateTime()
        )

        when:
        resource.plays(null, null)

        then:
        1 * playlistModule.getPlays(_) >> { PlaylistRequest playlistRequest ->
            assert playlistRequest.rangeStartTime
            assert acceptableStartRange.contains(playlistRequest.rangeStartTime.toDateTime())
            assert playlistRequest.rangeEndTime
            assert playlistRequest.rangeStartTime.plusDays(1) == playlistRequest.rangeEndTime
            return []
        }
        0 * _
    }

    def "Making a request with just a start time defaults the end time to 24 hours later"() {

        setup:
        LocalDateTime startTime = new LocalDateTime(2014, 1, 1, 12, 0)
        LocalDateTime expectedEndTime = startTime.plusDays(1)

        when:
        resource.plays(new DateTimeParam(startTime.toString()), null)

        then:
        1 * playlistModule.getPlays(_) >> { PlaylistRequest playlistRequest ->
            assert playlistRequest.rangeStartTime == startTime
            assert playlistRequest.rangeEndTime == expectedEndTime
            return []
        }
        0 * _
    }

    def "Requesting more than 24 hours at once returns error"() {

        setup:
        LocalDateTime startTime = new LocalDateTime(2014, 1, 1, 12, 0)
        LocalDateTime endTime = startTime.plusHours(25)

        when:
        resource.plays(new DateTimeParam(startTime.toString()), new DateTimeParam(endTime.toString()))

        then:
        final WebApplicationException exception = thrown()
        assert exception.response.status == HttpURLConnection.HTTP_BAD_REQUEST
        assert exception.response.entity == 'The requested date range cannot be greater than P1D'
    }

    def "Start date must be before end date"() {

        setup:
        LocalDateTime startTime = new LocalDateTime(2014, 1, 1, 12, 0)
        LocalDateTime endTime = startTime.minusHours(1)

        when:
        resource.plays(new DateTimeParam(startTime.toString()), new DateTimeParam(endTime.toString()))

        then:
        final WebApplicationException exception = thrown()
        assert exception.response.status == HttpURLConnection.HTTP_BAD_REQUEST
        assert exception.response.entity == 'rangeEndTime must be later than rangeStartTime'

    }

}
