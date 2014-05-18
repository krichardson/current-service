package net.krisr.current.dao

import net.krisr.current.domain.ArtistEntity
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions

class ArtistDAO extends AbstractDAO<ArtistEntity> {

    ArtistDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
    }

    ArtistEntity findByName(String name) {
        return criteria().add(Restrictions.eq('name', name)).uniqueResult() as ArtistEntity
    }

}
