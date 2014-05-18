package net.krisr.current.dao

import net.krisr.current.domain.ArtistEntity
import net.krisr.current.domain.SongEntity
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions

class SongDAO extends AbstractDAO<SongEntity> {

    SongDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
    }

    SongEntity findByArtistAndTitle(ArtistEntity artist, String title) {

        return criteria()
                    .add(Restrictions.eq('artist', artist))
                    .add(Restrictions.eq('title', title))
                    .uniqueResult() as SongEntity

    }

}
