package com.whatplayed.dao

import com.whatplayed.api.Artist
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(ArtistMapper)
interface ArtistDAO {

    @SqlQuery('select id as artist_id, name as artist_name from artist order by name limit :limit offset :offset')
    List<Artist> listArtists(@Bind('limit') Integer limit, @Bind('offset') Integer offset)

    @SqlQuery('select id as artist_id, name as artist_name from artist where id = :id')
    Artist findById(@Bind('id') Long id)

    @SqlQuery('select id as artist_id, name as artist_name from artist where name = :name')
    Artist findByName(@Bind('name') String name)

    @SqlUpdate('insert into artist (name) values (:name)')
    @GetGeneratedKeys
    Long create(@Bind('name') String name)

    @SqlUpdate('update artist set name = :name where id = :id')
    void update(@Bind('id') Long id, @Bind('name') String name)

}
