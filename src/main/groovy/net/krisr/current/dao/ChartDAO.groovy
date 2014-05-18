package net.krisr.current.dao

import net.krisr.current.domain.ChartEntity
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import org.joda.time.LocalDate

class ChartDAO extends AbstractDAO<ChartEntity> {

    ChartDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
    }

    ChartEntity findById(Long id) {
        return get(id)
    }

    ChartEntity findByDate(LocalDate date) {
        return criteria()
                    .add(Restrictions.eq('date', date))
                    .uniqueResult() as ChartEntity
    }

}