package com.whatplayed.jdbi

import org.joda.time.LocalDate
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.Argument

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types

/**
 * An {@link Argument} for Joda {@link LocalDate} objects.
 */
@SuppressWarnings(['JdbcStatementReference'])
public class JodaLocalDateArgument implements Argument {

    private final LocalDate value

    JodaLocalDateArgument(final LocalDate value) {
        this.value = value
    }

    @Override
    public void apply(final int position,
                      final PreparedStatement statement,
                      final StatementContext ctx) throws SQLException {
        if (value != null) {
            statement.setDate(position, new java.sql.Date(value.toDateTimeAtStartOfDay().millis))
        } else {
            statement.setNull(position, Types.DATE)
        }
    }
}
