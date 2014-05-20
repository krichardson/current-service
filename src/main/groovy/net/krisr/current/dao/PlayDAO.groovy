package net.krisr.current.dao

import net.krisr.current.domain.PlayEntity
import org.hibernate.SessionFactory
import org.hibernate.criterion.ProjectionList
import org.hibernate.criterion.Projections
import org.joda.time.LocalDateTime

class PlayDAO extends AbstractDAO<PlayEntity> {

    PlayDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
    }

    LocalDateTime findLastImportTime() {
        return criteria()
                .setProjection(maxPlayTimeProjection())
                .uniqueResult() as LocalDateTime
    }

    private static ProjectionList maxPlayTimeProjection() {
        return Projections.projectionList().add(Projections.max('playTime'))
    }

}
