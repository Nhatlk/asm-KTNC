package dao;

import java.util.List;

import config.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public abstract class BaseDAOImpl<K, T>
        implements BaseDAO<K, T> {

    protected final Class<T> entityClass;

    protected BaseDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T findById(K id) {

        try (EntityManager em = EMF.getEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public List<T> findAll() {

        try (EntityManager em = EMF.getEntityManager()) {

            String jpql = "SELECT e FROM "
                    + entityClass.getSimpleName() + " e";

            return em.createQuery(jpql, entityClass)
                    .getResultList();
        }
    }

    @Override
    public T insert(T entity) {

        try (EntityManager em = EMF.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                em.persist(entity);

                tx.commit();

                return entity;

            } catch (RuntimeException e) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }
    }

    @Override
    public T update(T entity) {

        try (EntityManager em = EMF.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                T updated = em.merge(entity);

                tx.commit();

                return updated;

            } catch (RuntimeException e) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }
    }

    @Override
    public boolean delete(K id) {

        try (EntityManager em = EMF.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                T entity = em.find(entityClass, id);

                if (entity == null) {
                    tx.rollback();
                    return false;
                }

                em.remove(entity);

                tx.commit();

                return true;

            } catch (RuntimeException e) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }
    }
}