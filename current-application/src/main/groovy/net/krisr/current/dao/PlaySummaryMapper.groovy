package net.krisr.current.dao

import net.krisr.current.api.Artist
import net.krisr.current.api.PlaySummary
import net.krisr.current.api.Song
import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

@SuppressWarnings('JdbcResultSetReference')
class PlaySummaryMapper implements ResultSetMapper<PlaySummary> {

    public PlaySummary map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        Artist artist = new Artist(
                id: r.getLong('artist_id'),
                name: r.getString('artist_name'),
        )
        Song song = new Song(
                id: r.getLong('song_id'),
                title: r.getString('song_title'),
                artist: artist,
        )
        PlaySummary playSummary = new PlaySummary(
                plays: r.getInt('plays'),
                earliestPlay: new LocalDateTime(r.getTimestamp('earliest_play_time')),
                latestPlay: new LocalDateTime(r.getTimestamp('latest_play_time')),
                song: song,
        )

        return playSummary

    }

}
