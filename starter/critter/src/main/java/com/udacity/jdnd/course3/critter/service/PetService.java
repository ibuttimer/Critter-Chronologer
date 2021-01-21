package com.udacity.jdnd.course3.critter.service;

import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.exception.InvalidPetException;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

import static com.udacity.jdnd.course3.critter.pet.Pet.OWNER_COL;

@Service
@Validated
public class PetService extends AbstractService<Pet> {

    CustomerService customerService;

    public PetService(AbstractRepository<Pet> repository) {
        super(repository);
    }

    public void setCustomerService(CustomerService customerService) {
        // for use as post construct setter to avoid circular dependency
        this.customerService = customerService;
    }

    public List<Pet> getPetsForOwnerInternal(long ownerId) {
        return repository.find(OWNER_COL, ownerId);
    }

    public List<Pet> getPetsForOwner(long ownerId) {
        return getPetsForOwnerInternal(ownerId);
    }

    @Override
    protected Pet getEntityInstance() {
        return new Pet();
    }

    /**
     * Validate an entity
     * Validation rules
     *  - must have name
     * @param entity - entity to validate
     * @return
     */
    @Override
    protected Pet validateInputEntity(Pet entity) {
        List<String> errors = Lists.newArrayList();
        if (entity.getType() == null) {
            entity.setType(PetType.UNKNOWN);
        }
        if (!StringUtils.hasText(entity.getName())) {
            errors.add("Name required");
        }
        if (entity.getBirthDate() == null) {
            entity.setBirthDate(LocalDate.MIN);
        }
        if (entity.getNotes() == null) {
            entity.setNotes("");
        }
        if (errors.size() > 0) {
            throw new InvalidPetException(errors);
        }
        return entity;
    }

    @Override
    protected Pet validateOutputEntity(Pet entity) {
        return entity;
    }

    @Override
    protected Pet convertProxies(Pet entity) {
        // convert id to owner
        return convertProxies(entity,
                entity.getOwner() == null ? 0 : entity.getOwner().getId());
    }

    private Pet convertProxies(Pet entity, Long ownerId) {
        // convert owner id to customer
        if (ownerId != 0) {
            entity.setOwner(
                customerService.getInternal(ownerId));
        }
        return entity;
    }
}
