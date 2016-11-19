package net.krisr.jdbi

import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.Argument

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Timestamp
import java.sql.Types

/**
 * An {@link Argument} for Joda {@link LocalDateTime} objects.
 */
@SuppressWarnings(['JdbcStatementReference'])
public class JodaLocalDateTimeArgument implements Argument {

    private final LocalDateTime value

    JodaLocalDateTimeArgument(final LocalDateTime value) {
        this.value = value
    }

    @Override
    public void apply(final int position,
                      final PreparedStatement statement,
                      final StatementContext ctx) throws SQLException {
        if (value != null) {
            statement.setTimestamp(position, new Timestamp(value.toDateTime().millis))
        } else {
            statement.setNull(position, Types.TIMESTAMP)
        }
    }
}
