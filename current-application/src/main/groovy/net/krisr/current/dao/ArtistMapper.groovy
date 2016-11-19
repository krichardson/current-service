package net.krisr.current.dao

import net.krisr.current.api.Artist
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

@SuppressWarnings('JdbcResultSetReference')
class ArtistMapper implements ResultSetMapper<Artist> {

    public Artist map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Artist artist = new Artist(
                id: r.getLong('artist_id'),
                name: r.getString('artist_name'),
        )
        return artist
    }

}
