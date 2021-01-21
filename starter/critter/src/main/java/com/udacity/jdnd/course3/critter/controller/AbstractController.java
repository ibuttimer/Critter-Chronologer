package com.udacity.jdnd.course3.critter.controller;


import com.udacity.jdnd.course3.critter.common.IDto;
import com.udacity.jdnd.course3.critter.common.IEntity;
import com.udacity.jdnd.course3.critter.service.AbstractService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.common.Creature.ID_COL;
import static com.udacity.jdnd.course3.critter.common.Creature.PROXY_COL;

/**
 * Base class for all controllers
 */
public abstract class AbstractController<E extends IEntity, DTO extends IDto> {

    protected AbstractService<E> service;

    public void setService(AbstractService<E> service) {
        this.service = service;
    }


    /**
     * Save object
     * @param dto - data transfer object representing entity
     * @return
     */
    public DTO save(DTO dto) {
        E entity = convertDtoToEntity(dto);
        entity = service.save(entity);
        return convertEntityToDto(entity);
    }

    /**
     * Get object
     * @param id - id of object to retrieve
     * @return
     */
    public DTO get(long id) {
        E entity = service.get(id);
        return convertEntityToDto(entity);
    }

    /**
     * Get all objects
     * @return
     */
    public List<DTO> getAll() {
        List<E> entities = service.getAll();
        return convertEntityToDto(entities);
    }

    /**
     * Update an object
     * @param dto - data transfer object representing entity
     * @return
     */
    public DTO update(DTO dto, long id) {
        E entity = service.get(id);
        return convertEntityToDto(
                service.update(
                        copyUpdateProperties(dto, entity)));
    }

    /**
     * Delete an object
     * @param id - id of object to remove
     * @return
     */
    public int delete(long id) {
        return service.delete(id);
    }

    /**
     * Get a new entity instance
     * @return
     */
    protected abstract E getEntityInstance();

    /**
     * Get a new data transfer object instance
     * @return
     */
    protected abstract DTO getDtoInstance();

    /**
     * Convert a data transfer object to an entity
     * @param dto - dto to convert
     * @return
     */
    public E convertDtoToEntity(DTO dto) {
        E entity = getEntityInstance();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    /**
     * Convert an entity to a data transfer object
     * @param entity - entity to convert
     * @return
     */
    public DTO convertEntityToDto(E entity) {
        DTO dto = getDtoInstance();
        BeanUtils.copyProperties(entity, dto, getEntityPropertiesToIgnore(entity).toArray(String[]::new));
        return dto;
    }

    /**
     * Convert a list of data transfer objects to entities
     * @param dtos - dto to convert
     * @return
     */
    public List<E> convertDtoToEntity(List<DTO> dtos) {
        return dtos.stream()
                .map(this::convertDtoToEntity)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of entities to data transfer objects
     * @param entities - entities to convert
     * @return
     */
    public List<DTO> convertEntityToDto(List<E> entities) {
        return entities.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Copy properties from a DTO to entity for update purposes
     * This function will copy values of the same name, but subclasses should override this method to add specific
     * handling if required.
     * @param dto - update object to copy from
     * @param entity - object to update
     * @return
     */
    protected E copyUpdateProperties(DTO dto, E entity) {
        BeanUtils.copyProperties(dto, entity, getUpdatePropertiesToIgnore(dto).toArray(String[]::new));
        return entity;
    }

    /**
     * Get the list of properties to ignore when doing an update.
     * Subclasses should override this method, ensuring to add the properties specific to those entities, to the
     * result of this function.
     * @param dto - update object to copy from
     * @return
     */
    public List<String> getUpdatePropertiesToIgnore(DTO dto) {
        // always ignore id in an update
        return List.of(ID_COL, PROXY_COL);
    }

    public List<String> getEntityPropertiesToIgnore(E entity) {
        // always ignore proxy
        return List.of(PROXY_COL);
    }

    protected List<Long> makeProxyIds(List<? extends IEntity> entityList) {
        List<Long> idList = null;
        if (entityList != null) {
            idList = entityList.stream()
                    .map(IEntity::getId)
                    .collect(Collectors.toList());
        }
        return idList;
    }
}
