package com.whatplayed.dao

import com.whatplayed.api.Source
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

@SuppressWarnings('JdbcResultSetReference')
class SourceMapper implements ResultSetMapper<Source> {

    public Source map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Source artist = new Source(
                id: r.getLong('source_id'),
                name: r.getString('source_name'),
        )
        return artist
    }

}
