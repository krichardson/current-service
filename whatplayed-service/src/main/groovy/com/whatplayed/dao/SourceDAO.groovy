package com.whatplayed.dao

import com.whatplayed.api.Source
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(SourceMapper)
interface SourceDAO {

    @SqlQuery('select id as source_id, name as source_name from source order by name limit :limit offset :offset')
    List<Source> listSources(@Bind('limit') Integer limit, @Bind('offset') Integer offset)

    @SqlQuery('select id as source_id, name as source_name from source where id = :id')
    Source findById(@Bind('id') Long id)

    @SqlQuery('select id as source_id, name as source_name from source where name = :name')
    Source findByName(@Bind('name') String name)

    @SqlUpdate('insert into source (name) values (:name)')
    @GetGeneratedKeys
    Long create(@Bind('name') String name)

    @SqlUpdate('update source set name = :name where id = :id')
    void update(@Bind('id') Long id, @Bind('name') String name)

}
