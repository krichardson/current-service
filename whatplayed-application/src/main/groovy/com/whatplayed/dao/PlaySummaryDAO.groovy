package com.whatplayed.dao

import com.whatplayed.api.PlaySummary
import org.joda.time.LocalDateTime
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(PlaySummaryMapper)
public interface PlaySummaryDAO {

    @SqlQuery('''
    select a.id as artist_id, a.name as artist_name,
    s.id as song_id, s.title as song_title,
    count(1) as plays, min(p.play_time) as earliest_play_time, max(p.play_time) as latest_play_time
    from play p
        join song s
            on p.song_id = s.id
        join artist a
            on s.artist_id = a.id
    where p.play_time >= :startTime
            and p.play_time <= :endTime
    group by a.id, a.name, s.id, s.title
            order by count(1) desc, artist_name asc, song_title asc
            limit :limit
            offset :offset
    ''')
    List<PlaySummary> findTopPlaysBetween(@Bind('startTime') LocalDateTime startTime,
                                          @Bind('endTime') LocalDateTime endTime,
                                          @Bind('limit') Integer limit,
                                          @Bind('offset') Integer offset)

}