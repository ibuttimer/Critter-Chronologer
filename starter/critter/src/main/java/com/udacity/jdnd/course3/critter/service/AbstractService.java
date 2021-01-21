package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.common.IEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.FetchType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for services
 * @param <E> type of entity
 */
@Service
@Validated
@Transactional
public abstract class AbstractService<E extends IEntity> {

    protected AbstractRepository<E> repository;

    /**
     * Constructor
     * @param repository - repository for service
     */
    public AbstractService(AbstractRepository<E> repository) {
        this.repository = repository;
    }

    /**
     * Save object to repository
     * @param entity - entity to save
     * @return
     */
    public E save(E entity) {
        convertProxies(
                validateInputEntity(entity));
        repository.persist(
                validateOutputEntity(entity));
        return entity;
    }

    /**
     * Update an object
     * @param entity - entity to update
     * @return
     * See <a href="https://vladmihalcea.com/jpa-persist-and-merge/">The redundant save anti-pattern</a>
     */
    public E update(E entity) {
        convertProxies(
                validateInputEntity(entity));
        return validateOutputEntity(entity);
    }

    /**
     * Get object
     * @param id - id of object to retrieve
     * @return
     */
    public E get(long id) {
        return getInternal(id);
    }

    /**
     * Get object, (only accessible to other services within this package)
     * @param id - id of object to retrieve
     * @return
     */
    E getInternal(long id) {
        return repository.find(id);
    }

    /**
     * Get object
     * @param id - id of object to retrieve
     * @param fetchType - whether object should be fully populated (FetchType.EAGER) or not (FetchType.LAZY)
     * @return
     */
    public E get(long id, FetchType fetchType) {
        E entity = getInternal(id);
        if (fetchType == FetchType.EAGER) {
            populateEntity(entity);
        }
        return entity;
    }

    /**
     * Get a list of objects, (only accessible to other services within this package)
     * @param ids - ids of object to retrieve
     * @return
     */
    List<E> getInternal(List<Long> ids) {
        List<E> list;
        if (ids != null) {
            list = repository.find(ids);
        } else {
            list = List.of();
        }
        return list;
    }

    /**
     * Get all objects
     * @return
     */
    public List<E> getAll() {
        return repository.findAll();
    }

    /**
     * Delete an object
     * @param id - id of object to remove
     * @return
     */
    public int delete(long id) {
        return repository.delete(id);
    }

    /**
     * Get a new entity instance
     * @return
     */
    protected abstract E getEntityInstance();

    /**
     * Perform preprocessing validation
     * @param entity - dto to validate
     * @return
     */
    protected abstract E validateInputEntity(E entity);

    /**
     * Perform postprocessing validation to make sure the entity is safe to pass to the data layer
     * @param entity - dto to validate
     * @return
     */
    protected abstract E validateOutputEntity(E entity);

    /**
     * Convert proxy objects to their corresponding object
     * @return
     */
    protected abstract E convertProxies(E entity);


    public E populateEntity(E entity) {
        return entity;
    }

    /**
     * Get a list of ids from proxy objects
     * @param proxyList - list of proxy objects
     * @return
     */
    protected List<Long> getProxyIds(List<? extends IEntity> proxyList) {
        List<Long> idList = null;
        if (proxyList != null) {
            if (proxyList.size() != proxyList.stream()
                                        .filter(IEntity::isProxy)
                                        .count()) {
                throw new IllegalStateException("Non-proxy object in proxy id list");
            }
            idList = proxyList.stream()
                    .map(IEntity::getId)
                    .collect(Collectors.toList());
        }
        return idList;
    }
}
