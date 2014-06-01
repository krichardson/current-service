package net.krisr.jdbi

import org.joda.time.LocalDate
import org.skife.jdbi.v2.util.TypedMapper

import java.sql.ResultSet
import java.sql.SQLException

/**
 * A {@link TypedMapper} to map Joda {@link LocalDate} objects.
 */
public class JodaLocalDateMapper extends TypedMapper<LocalDate> {

    @Override
    protected LocalDate extractByName(final ResultSet r, final String name) throws SQLException {
        return new LocalDate(r.getDate(name))
    }

    @Override
    protected LocalDate extractByIndex(final ResultSet r, final int index) throws SQLException {
        return new LocalDate(r.getDate(index))
    }
}
