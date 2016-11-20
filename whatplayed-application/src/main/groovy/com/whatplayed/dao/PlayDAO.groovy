package com.whatplayed.dao

import com.whatplayed.api.Play
import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(PlayMapper)
interface PlayDAO {

    @SqlQuery('''
        select p.id as play_id, p.play_time,
            s.id as song_id, s.title as song_title,
            a.id as artist_id, a.name as artist_name,
            sc.id as source_id, sc.name as source_name
        from play p
            join song s
                on p.song_id = s.id
            join artist a
                on s.artist_id = a.id
            join source sc
                on p.source_id = sc.id
        where p.id = :id
    ''')
    Play findById(@Bind('id') Long id)

    @SqlQuery('select max(play_time) as latest_play_time from play')
    LocalDateTime findLatestPlayTime()

    @SqlQuery('''
        select p.id as play_id, p.play_time,
            s.id as song_id, s.title as song_title,
            a.id as artist_id, a.name as artist_name,
            sc.id as source_id, sc.name as source_name
        from play p
            join song s
                on p.song_id = s.id
            join artist a
                on s.artist_id = a.id
            join source sc
                on p.source_id = sc.id
        where p.play_time >= :startTime
            and p.play_time <= :endTime
            and p.source_id = :sourceId
            order by p.play_time desc
    ''')
    List<Play> findPlaysBetween(@Bind('sourceId') Long sourceId,
                                @Bind('startTime') LocalDateTime startTime,
                                @Bind('endTime') LocalDateTime endTime)


    @SqlUpdate('insert into play (play_time, song_id, source_id) values (:playTime, :songId, :sourceId)')
    @GetGeneratedKeys
    Long create(@Bind('playTime') LocalDateTime playTime,
                @Bind('songId') Long songId,
                @Bind('sourceId') Long sourceId)

}
