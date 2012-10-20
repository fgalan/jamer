package com.mycompany.server.dao;

import com.mycompany.server.entity.DummyEntity;

/**
 * Plain DAO which provides only {@link com.mycompany.server.dao.impl.GenericHibernateDAOImpl} methods
 */
public interface DummyDAO extends GenericDAO<DummyEntity, Long> {
}
