package com.mycompany.server.dao.impl;

import org.springframework.stereotype.Repository;

import com.mycompany.server.dao.DummyDAO;
import com.mycompany.server.entity.DummyEntity;

/**
 * Plain DAO which provides only {@link com.mycompany.server.dao.impl.GenericHibernateDAOImpl} methods
 */
@Repository
public class DummyDAOImpl extends GenericHibernateDAOImpl<DummyEntity, Long> implements DummyDAO {
    
}
