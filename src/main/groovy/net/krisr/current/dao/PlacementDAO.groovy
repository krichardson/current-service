package net.krisr.current.dao

import net.krisr.current.api.Placement
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(PlacementMapper)
interface PlacementDAO {

    @SqlQuery('''
        select p.position,
            s.id as song_id, s.title as song_title,
            a.id as artist_id, a.name as artist_name
        from placement p
            join song s
                on p.song_id = s.id
            join artist a
                on s.artist_id = a.id
        where p.chart_id = :chartId
        ''')
    List<Placement> findAllByChartId(@Bind('chartId') Long chartId)

    @SqlUpdate('insert into placement (chart_id, position, song_id) values (:chartId, :position, :songId)')
    @GetGeneratedKeys
    Long create(@Bind('chartId') Long chartId,
                @Bind('position') Integer position,
                @Bind('songId') Long songId)

}
