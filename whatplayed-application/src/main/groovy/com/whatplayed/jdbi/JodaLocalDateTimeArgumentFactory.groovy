package com.whatplayed.jdbi

import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.Argument
import org.skife.jdbi.v2.tweak.ArgumentFactory

/**
 * An {@link ArgumentFactory} for Joda {@link LocalDateTime} arguments.
 */
public class JodaLocalDateTimeArgumentFactory implements ArgumentFactory<LocalDateTime> {

    @Override
    @SuppressWarnings('Instanceof')
    public boolean accepts(final Class<?> expectedType,
                           final Object value,
                           final StatementContext ctx) {
        return value instanceof LocalDateTime
    }

    @Override
    public Argument build(final Class<?> expectedType,
                          final LocalDateTime value,
                          final StatementContext ctx) {
        return new JodaLocalDateTimeArgument(value)
    }
}

