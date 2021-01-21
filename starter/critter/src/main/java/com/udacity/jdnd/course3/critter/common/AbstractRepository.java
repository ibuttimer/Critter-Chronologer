package com.udacity.jdnd.course3.critter.common;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.exception.NoEntityResultException;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import static com.udacity.jdnd.course3.critter.common.Creature.ID_COL;

@Repository
@Transactional
public class AbstractRepository<T extends IEntity> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected Class<T> entityClass;

    protected void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Make an instance managed and persistent.
     * @param entry - instance to persist
     */
    public void persist(T entry) {
        entityManager.persist(entry);
    }

    /**
     * Find by key
     * @param id - key
     * @return
     */
    public T find(long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            throw new NoEntityResultException(
                    String.format("No results found for id: [%s]", id));
        }
        return entity;
    }

    /**
     * Find by list of keys
     * @param ids - keys
     * @return
     */
    public List<T> find(List<Long> ids) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        // use compound OR predicate
        query.select(root)
                .where(builder.or(
                        ids.stream()
                            .map(id -> builder.equal(root.get(ID_COL), id))
                            .toArray(Predicate[]::new)));

        List<T> entities = entityManager.createQuery(query).getResultList();
        if (entities.size() != ids.size()) {
            List<Long> notFound = Lists.newArrayList();
            for (Long id : ids) {
                if (entities.stream()
                        .map(IId::getId)
                        .noneMatch(id::equals)) {
                    notFound.add(id);
                }
            }
            throw new NoEntityResultException(
                    String.format("No results found for id(s): [%s]", Joiner.on(',').join(notFound)));
        }
        return entities;
    }

    /**
     * Find by string column value
     * @param name - name
     * @return
     */
    public List<T> find(String column, String name) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        query.select(root)
                .where(builder.equal(
                        builder.upper(root.get(column)), name.toUpperCase()));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find by value in a collection
     * @param value - value to find
     * @return
     */
    public <E> List<T> findInCollection(String column, E value) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        query.select(root)
                .where(builder.isMember(value, root.get(column)));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find by values in a collection
     * @param values - values to find
     * @return
     */
    public <E> List<T> findInCollection(String column, List<E> values) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        query.select(root)
                .where(values.stream()
                        .map(e -> builder.isMember(e, root.get(column)))
                        .toArray(Predicate[]::new));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find by values in a collection
     * @param values - values to find
     * @return
     */
    public <E> List<T> findInCollection(String column, Set<E> values) {
        return findInCollection(column, Lists.newArrayList(values));
    }

    /**
     * Find by value
     * @param value - value to find
     * @return
     */
    public <E> List<T> find(String column, E value) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        query.select(root)
                .where(builder.equal(
                        root.get(column), value));

        return entityManager.createQuery(query).getResultList();
    }


    /**
     * Find all
     * @return
     */
    public List<T> findAll() {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        query.select(root);

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Merge the state of the given entity into the current persistence context.
     * @param entry - entity
     * @return
     */
    public T merge(T entry) {
        return entityManager.merge(entry);
    }

    /**
     * Delete by id
     * @param id - id of entity to delete
     * @return
     */
    public int delete(long id) {

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaDelete<T> query = builder.createCriteriaDelete(entityClass);
        Root<T> root = query.from(entityClass);

        query.where(
                builder.equal(root.get(ID_COL), id)
        );

        return entityManager.createQuery(query).executeUpdate();
    }

    /**
     * Flush the repository
     */
    public void flush() {
        entityManager.flush();
    }

    /**
     * Clear the repository
     */
    public void clear() {
        entityManager.clear();
    }

    /**
     * Get table name
     * @return
     */
    public String getTableNames() {
        /**
         * Based on https://stackoverflow.com/a/51556465
         */
        String tableName = null;
        try {
            // get the JPA session implementation
            SessionImpl session = entityManager.unwrap(SessionImpl.class);

            EntityPersister persister = session.getEntityPersister(null, entityClass.getConstructor().newInstance());
            if (persister instanceof AbstractEntityPersister) {
                String[] tableNames = ((AbstractEntityPersister)persister).getTableNames();
                // TODO is this the best way?
                // if there are multiple tables in the hierarchy, this entity's should be the last
                tableName = tableNames[tableNames.length - 1];
            } else {
                throw new UnsupportedOperationException("Unknown entity persister: " + persister.getClass().getCanonicalName());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return tableName;
    }

    /**
     * Get the row count
     * @return
     */
    public long count() {
        long count = 0;
        Object result = entityManager.createNativeQuery("SELECT COUNT(1) FROM " + getTableNames()).getSingleResult();
        if (result instanceof Number) {
            count = ((Number)result).longValue();
        } else {
            throw new UnsupportedOperationException("Unknown result type: " + result.getClass().getCanonicalName());
        }
        return count;
    }


    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getEntityManagerFactory().getCriteriaBuilder();
    }

}
