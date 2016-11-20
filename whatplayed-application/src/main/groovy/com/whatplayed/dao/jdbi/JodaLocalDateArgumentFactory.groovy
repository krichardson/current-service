package com.whatplayed.dao.jdbi

import org.joda.time.LocalDate
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.Argument
import org.skife.jdbi.v2.tweak.ArgumentFactory

/**
 * An {@link ArgumentFactory} for Joda {@link LocalDate} arguments.
 */
public class JodaLocalDateArgumentFactory implements ArgumentFactory<LocalDate> {

    @Override
    @SuppressWarnings('Instanceof')
    public boolean accepts(final Class<?> expectedType,
                           final Object value,
                           final StatementContext ctx) {
        return value instanceof LocalDate
    }

    @Override
    public Argument build(final Class<?> expectedType,
                          final LocalDate value,
                          final StatementContext ctx) {
        return new JodaLocalDateArgument(value)
    }
}
