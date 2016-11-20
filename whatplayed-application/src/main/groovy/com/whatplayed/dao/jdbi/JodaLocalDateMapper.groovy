package com.whatplayed.dao.jdbi

import org.joda.time.LocalDate
import org.skife.jdbi.v2.util.TypedMapper

import java.sql.ResultSet
import java.sql.SQLException

/**
 * A {@link TypedMapper} to map Joda {@link LocalDate} objects.
 */
@SuppressWarnings(['JdbcResultSetReference'])
public class JodaLocalDateMapper extends TypedMapper<LocalDate> {

    @Override
    protected LocalDate extractByName(final ResultSet r, final String name) throws SQLException {
        Date date = r.getDate(name)
        if (date) {
            return new LocalDate(date)
        }
        return null
    }

    @Override
    protected LocalDate extractByIndex(final ResultSet r, final int index) throws SQLException {
        Date date = r.getDate(index)
        if (date) {
            return new LocalDate(date)
        }
        return null
    }
}
