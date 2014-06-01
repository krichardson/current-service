package net.krisr.current.dao

import net.krisr.current.api.Song
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(SongMapper)
interface SongDAO {

    @SqlQuery('''
        select s.id as song_id, s.title as song_title,
            a.id as artist_id, a.name as artist_name
        from song s
            join artist a
                on s.artist_id = a.id
        where s.id = :id
    ''')
    Song findById(@Bind('id') Long id)

    @SqlQuery('''
        select s.id as song_id, s.title as song_title,
            a.id as artist_id, a.name as artist_name
        from song s
            join artist a
                on s.artist_id = a.id
        where s.artist_id = :artistId
            and s.title = :title
    ''')
    Song findByArtistAndTitle(@Bind('artistId') Long artistId,
                              @Bind('title') String title)

    @SqlUpdate('insert into song (title, artist_id) values (:title, :artistId)')
    @GetGeneratedKeys
    Long create(@Bind('title') String title, @Bind('artistId') Long artistId)

    @SqlUpdate('update song set title = :title, artist_id = :artistId where id = :id')
    void update(@Bind('id') Long id,
                @Bind('name') String title,
                @Bind('artistId') Long artistId)

}
