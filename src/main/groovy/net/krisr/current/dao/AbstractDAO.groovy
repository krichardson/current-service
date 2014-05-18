package net.krisr.current.dao

import org.hibernate.SessionFactory

abstract class AbstractDAO<E> extends com.yammer.dropwizard.hibernate.AbstractDAO<E> {

    AbstractDAO(SessionFactory sessionFactory) {
        super(sessionFactory)
    }

    public E createOrUpdate(E entity) {
        return persist(entity)
    }

}
