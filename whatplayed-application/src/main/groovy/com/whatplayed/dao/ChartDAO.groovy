package com.whatplayed.dao

import com.whatplayed.api.Chart
import org.joda.time.LocalDate
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(ChartMapper)
interface ChartDAO {

    @SqlQuery('select id as chart_id, date as chart_date from chart order by date desc')
    List<Chart> findAll()

    @SqlQuery('''
        select c.id as chart_id, c.date as chart_date
        from chart c
        where c.id = :id
        ''')
    Chart findById(@Bind('id') Long id)

    @SqlQuery('''
        select c.id as chart_id, c.date as chart_date
        from chart c
        where c.date = :date
        ''')
    Chart findByDate(@Bind('date') LocalDate date)

    @SqlUpdate('insert into chart (date) values (:date)')
    @GetGeneratedKeys
    Long create(@Bind('date') LocalDate date)

    @SqlUpdate('update chart set date = :date where id = :id')
    void update(@Bind('id') Long id, @Bind('date') LocalDate date)
}