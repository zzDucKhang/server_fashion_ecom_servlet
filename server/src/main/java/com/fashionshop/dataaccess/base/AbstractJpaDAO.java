package com.fashionshop.dataaccess.base;


import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractJpaDAO<T, ID> implements GenericDAO<T, ID> {
    protected final Class<T> entityClass;

    protected AbstractJpaDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R execute(Function<EntityManager, R> function) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return function.apply(em);
        } finally {
            em.close();
        }
    }

    protected <R> R executeTransaction(Function<EntityManager, R> function) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public T save(T entity) {
        return executeTransaction(em -> {
            em.persist(entity);
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        return executeTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(ID id) {
        executeTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            return null;
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        return execute(em -> Optional.ofNullable(em.find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        return execute(em -> em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList());
    }

    @Override
    public List<T> findPaginated(int page, int limit) {
        return execute(em -> {
            TypedQuery<T> query = em.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    @Override
    public long count() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class
        ).getSingleResult());
    }
}