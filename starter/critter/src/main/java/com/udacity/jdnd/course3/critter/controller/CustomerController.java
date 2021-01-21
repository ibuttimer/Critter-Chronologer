package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.common.Views;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.common.Creature.NAME_COL;
import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.user.Customer.NOTES_COL;
import static com.udacity.jdnd.course3.critter.user.Customer.PHONE_NUMBER_COL;
import static com.udacity.jdnd.course3.critter.user.CustomerDTO.PET_IDS_COL;

/**
 * Handles web requests related to Users.
 */
@RestController
@RequestMapping(USER_URL)
public class CustomerController extends AbstractController<Customer, CustomerDTO> {

    @Autowired
    CustomerService customerService;

    @PostConstruct
    void init() {
        setService(customerService);
    }

    @JsonView(Views.Public.class)
    @PostMapping(CUSTOMER_POST_URL)
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return save(customerDTO);
    }

    @JsonView(Views.Public.class)
    @GetMapping(CUSTOMER_GET_URL)
    public List<CustomerDTO> getAllCustomers(){
        return getAll();
    }

    @JsonView(Views.Public.class)
    @GetMapping(CUSTOMER_GET_ID_URL)
    public CustomerDTO getCustomerById(@PathVariable long customerId){
        return get(customerId);
    }

    @JsonView(Views.Public.class)
    @GetMapping(CUSTOMER_GET_BY_PET_URL)
    public CustomerDTO getCustomerByPet(@PathVariable long petId){
        return convertEntityToDto(
                customerService.getOwnerByPet(petId));
    }

    @JsonView(Views.Public.class)
    @GetMapping(OWNER_GET_BY_PET_URL)
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        return getCustomerByPet(petId);
    }



    @Override
    protected Customer getEntityInstance() {
        return new Customer();
    }

    @Override
    protected CustomerDTO getDtoInstance() {
        return new CustomerDTO();
    }

    @Override
    public Customer convertDtoToEntity(CustomerDTO dto) {
        Customer entity = super.convertDtoToEntity(dto);
        // convert ids to pet proxies
        if (dto.getPetIds() != null) {
            entity.setPets(
                dto.getPetIds().stream()
                    .map(Pet::proxy)
                    .collect(Collectors.toList())
            );
        }
        return entity;
    }

    @Override
    public CustomerDTO convertEntityToDto(Customer entity) {
        CustomerDTO dto = super.convertEntityToDto(entity);
        // convert pets to ids
        dto.setPetIds(
            makeProxyIds(entity.getPets())
        );
        return dto;
    }

    @Override
    protected Customer copyUpdateProperties(CustomerDTO dto, Customer entity) {
        super.copyUpdateProperties(dto, entity);
        return dtoToEntityTranslate(dto, entity);
    }

    protected Customer dtoToEntityTranslate(CustomerDTO dto, Customer entity) {
        // convert pet ids to pet proxies
        if (dto.getPetIds() != null) {
            entity.setPets(
                dto.getPetIds().stream()
                    .map(Pet::proxy)
                    .collect(Collectors.toList())
            );
        }
        return entity;
    }

    @Override
    public List<String> getUpdatePropertiesToIgnore(CustomerDTO dto) {
        List<String> list = Lists.newArrayList(super.getUpdatePropertiesToIgnore(dto));
        if (dto.getName() == null) {
            list.add(NAME_COL);
        }
        if (dto.getPhoneNumber() == null) {
            list.add(PHONE_NUMBER_COL);
        }
        if (dto.getNotes() == null) {
            list.add(NOTES_COL);
        }
        if (dto.getPetIds() == null) {
            list.add(PET_IDS_COL);
        }
        return list;
    }

}
