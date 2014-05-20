package net.krisr.current.dao

import net.krisr.current.domain.ChartEntity
import net.krisr.current.domain.PlacementEntity
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import org.joda.time.LocalDate

class ChartDAO extends AbstractDAO<ChartEntity> {

    private final PlacementDAO placementDAO

    ChartDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
        placementDAO = new PlacementDAO(sessionFactory)
    }

    ChartEntity findById(Long id) {
        return get(id)
    }

    ChartEntity findByDate(LocalDate date) {
        return criteria()
                    .add(Restrictions.eq('date', date))
                    .uniqueResult() as ChartEntity
    }

    PlacementEntity createPlacement(PlacementEntity placementEntity) {
        return placementDAO.createOrUpdate(placementEntity)
    }

}