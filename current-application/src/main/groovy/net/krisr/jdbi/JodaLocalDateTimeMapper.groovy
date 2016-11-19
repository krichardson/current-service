package net.krisr.jdbi

import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.util.TypedMapper

import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp

/**
 * A {@link TypedMapper} to map Joda {@link LocalDateTime} objects.
 */
@SuppressWarnings(['JdbcResultSetReference'])
public class JodaLocalDateTimeMapper extends TypedMapper<LocalDateTime> {

    @Override
    protected LocalDateTime extractByName(final ResultSet r, final String name) throws SQLException {
        Timestamp timestamp = r.getTimestamp(name)
        if (timestamp) {
            return new LocalDateTime(timestamp.time)
        }
        return null
    }

    @Override
    protected LocalDateTime extractByIndex(final ResultSet r, final int index) throws SQLException {
        Timestamp timestamp = r.getTimestamp(index)
        if (timestamp) {
            return new LocalDateTime(timestamp.time)
        }
        return null
    }
}

