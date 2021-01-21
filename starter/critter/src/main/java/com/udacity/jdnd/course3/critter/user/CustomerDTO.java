package com.udacity.jdnd.course3.critter.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.udacity.jdnd.course3.critter.common.IDto;
import com.udacity.jdnd.course3.critter.common.Views;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the form that customer request and response data takes. Does not map
 * to the database directly.
 */
public class CustomerDTO implements IDto, ICustomer.ICustomerDTO {

    public static final String PET_IDS_COL = "petIds";

    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    private String phoneNumber;

    @JsonView(Views.Public.class)
    private String notes;

    @JsonView(Views.Public.class)
    private List<Long> petIds;

    public CustomerDTO() {
    }

    public CustomerDTO(long id, String name, String phoneNumber, String notes, List<Long> petIds) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        this.petIds = petIds;
    }

    public static CustomerDTO of(long id, String name, String phoneNumber, String notes, List<Long> petIds) {
        return new CustomerDTO(id, name, phoneNumber, notes, petIds);
    }

    @JsonView(Views.Internal.class)
    @Override
    public boolean isProxy() {
        return true;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public List<Long> getPetIds() {
        return petIds;
    }

    @Override
    public void setPetIds(List<Long> petIds) {
        this.petIds = petIds;
    }

    public void removePetId(long petId) {
        setPetIds(petIds.stream()
                    .filter(id -> id != petId)
                    .collect(Collectors.toList()));
    }

    public void addPetId(long petId) {
        List<Long> list = new ArrayList<>(petIds);
        list.add(petId);
        setPetIds(list);
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", notes='" + notes + '\'' +
                ", petIds=" + petIds +
                '}';
    }
}
