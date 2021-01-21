package com.udacity.jdnd.course3.critter.service;

import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.exception.InvalidCustomerException;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Validated
public class CustomerService extends AbstractService<Customer> {

    @Autowired
    PetService petService;

    @Autowired
    CustomerRepository customerRepository;

    public CustomerService(CustomerRepository repository) {
        super(repository);
    }

    @PostConstruct
    public void init() {
        // circular dependency between CustomerService & PetService, so use post construct setter
        petService.setCustomerService(this);
    }

    /**
     * Get the owner for the specified pet
     * @param petId - id of pet
     * @return
     */
    public Customer getOwnerByPet(long petId){
        return customerRepository.findOwnerByPet(petId);
    }

    @Override
    protected Customer getEntityInstance() {
        return new Customer();
    }

    /**
     * Validate an entity
     * Validation rules
     *  - must have name
     *  - must have phone number
     * The supplied ids are verified in {@link AbstractRepository#find(List)}.
     * @param entity - entity to validate
     * @return
     */
    @Override
    protected Customer validateInputEntity(Customer entity) {
        List<String> errors = Lists.newArrayList();
        if (!StringUtils.hasText(entity.getName())) {
            errors.add("Name required");
        }
        if (!StringUtils.hasText(entity.getPhoneNumber())) {
            errors.add("Phone number required");
        }
        if (entity.getNotes() == null) {
            entity.setNotes("");
        }
        if (entity.getPets() == null) {
            entity.setPets(List.of());
        }
        if (errors.size() > 0) {
            throw new InvalidCustomerException(errors);
        }
        return entity;
    }

    @Override
    protected Customer validateOutputEntity(Customer entity) {
        return entity;
    }

    @Override
    protected Customer convertProxies(Customer entity) {
        // convert proxies for pets
        return convertProxies(entity, getProxyIds(entity.getPets()));
    }

    private Customer convertProxies(Customer entity, List<Long> petIds) {
        // convert ids to pets
        entity.setPets(
            petService.getInternal(petIds)
        );
        return entity;
    }

    @Override
    public Customer populateEntity(Customer entity) {
        entity.setPets(
            petService.getPetsForOwnerInternal(entity.getId())
        );
        return entity;
    }
}
