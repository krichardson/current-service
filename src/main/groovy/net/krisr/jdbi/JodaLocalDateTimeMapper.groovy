package net.krisr.jdbi

import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.util.TypedMapper

import java.sql.ResultSet
import java.sql.SQLException

/**
 * A {@link TypedMapper} to map Joda {@link LocalDateTime} objects.
 */
public class JodaLocalDateTimeMapper extends TypedMapper<LocalDateTime> {

    @Override
    protected LocalDateTime extractByName(final ResultSet r, final String name) throws SQLException {
        return new LocalDateTime(r.getTimestamp(name).time)
    }

    @Override
    protected LocalDateTime extractByIndex(final ResultSet r, final int index) throws SQLException {
        return new LocalDateTime(r.getTimestamp(index).time)
    }
}

