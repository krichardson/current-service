package net.krisr.current.dao

import net.krisr.current.api.Chart
import org.joda.time.LocalDate
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

@SuppressWarnings('JdbcResultSetReference')
class ChartMapper implements ResultSetMapper<Chart> {

    public Chart map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        Chart chart = new Chart(
                id: r.getLong('chart_id'),
                date: new LocalDate(r.getDate('chart_date')),
                placements: []
        )
        return chart

    }

}
