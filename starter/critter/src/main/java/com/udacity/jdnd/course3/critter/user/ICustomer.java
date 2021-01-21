package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.common.ICreature;
import com.udacity.jdnd.course3.critter.pet.Pet;

import java.util.List;

public interface ICustomer extends ICreature {
    long getId();

    void setId(long id);

    String getName();

    void setName(String name);

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    String getNotes();

    void setNotes(String notes);


    public interface ICustomerEntity extends ICustomer {
        void setPets(List<Pet> pets);

        List<Pet> getPets();
    }

    public interface ICustomerDTO extends ICustomer {
        void setPetIds(List<Long> petIds);

        List<Long> getPetIds();
    }
}
