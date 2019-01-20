package com.whatplayed.dao

import com.whatplayed.api.Artist
import com.whatplayed.api.Play
import com.whatplayed.api.Song
import com.whatplayed.api.Source
import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

@SuppressWarnings('JdbcResultSetReference')
class PlayMapper implements ResultSetMapper<Play> {

    Play map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Source source = new Source(
                id: r.getLong('source_id'),
                name: r.getString('source_name'),
        )
        Artist artist = new Artist(
                id: r.getLong('artist_id'),
                name: r.getString('artist_name'),
        )
        Song song = new Song(
                id: r.getLong('song_id'),
                title: r.getString('song_title'),
                artist: artist,
        )
        Play play = new Play(
                id: r.getLong('play_id'),
                playTime: new LocalDateTime(r.getTimestamp('play_time')),
                song: song,
                source: source,
        )
        return play
    }

}
