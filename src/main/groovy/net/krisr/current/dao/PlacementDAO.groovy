package net.krisr.current.dao

import net.krisr.current.domain.PlacementEntity
import org.hibernate.SessionFactory

class PlacementDAO extends AbstractDAO<PlacementEntity> {

    PlacementDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
    }

}
