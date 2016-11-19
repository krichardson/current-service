package net.krisr.current.dao

import net.krisr.current.api.Artist
import net.krisr.current.api.Song
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

@SuppressWarnings('JdbcResultSetReference')
class SongMapper implements ResultSetMapper<Song> {

    public Song map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Artist artist = new Artist(
                id: r.getLong('artist_id'),
                name: r.getString('artist_name'),
        )
        Song song = new Song(
                id: r.getLong('song_id'),
                title: r.getString('song_title'),
                artist: artist,
        )
        return song
    }

}
