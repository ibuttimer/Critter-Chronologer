package com.udacity.jdnd.course3.critter.user;

import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.common.Creature;
import com.udacity.jdnd.course3.critter.common.IEntity;
import com.udacity.jdnd.course3.critter.pet.Pet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

import static com.udacity.jdnd.course3.critter.pet.Pet.OWNER_COL;

@Entity
public class Customer extends Creature implements IEntity, ICustomer.ICustomerEntity {

    public static final String PHONE_NUMBER_COL = "phoneNumber";
    public static final String NOTES_COL = "notes";
    public static final String PETS_COL = "pets";

    private String phoneNumber;
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = OWNER_COL, cascade = CascadeType.ALL)
    private List<Pet> pets;

    public Customer() {
        super();
    }

    public Customer(long id, String name, String phoneNumber, String notes, List<Pet> pets) {
        super(id, name);
        init(phoneNumber, notes, pets);
    }

    private void init(String phoneNumber, String notes, List<Pet> pets) {
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        setPets(pets);
    }

    public static Customer of() {
        return new Customer();
    }

    public static Customer proxy(long id) {
        Customer customer = new Customer();
        customer.proxy = true;
        customer.setId(id);
        return customer;
    }

    public static Customer of(long id, String name, String phoneNumber, String notes, List<Pet> pets) {
        return new Customer(id, name, phoneNumber, notes, pets);
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
    public List<Pet> getPets() {
        return pets;
    }

    @Override
    public void setPets(List<Pet> pets) {
        if (!Objects.equals(this.pets, pets)) {
            this.pets = Lists.newArrayList(pets);
            pets.forEach(pet -> pet.setOwner(this));
        }
    }

    public void removePet(Pet pet) {
        if (pets != null) {
            if (pets.remove(pet)) {
                pet.setOwner(null);
            }
        }
    }

    public void addPet(Pet pet) {
        if (pets == null) {
            pets = Lists.newArrayList();
        }
        if (!pets.contains(pet)) {
            pets.add(pet);
            pet.setOwner(this);
        }
    }

    @Override
    public String toString() {
        return super.toString().replace('}', ',') +
                " phoneNumber='" + phoneNumber + '\'' +
                ", notes='" + notes + '\'' +
                ", pets=" + pets +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(getPhoneNumber(), customer.getPhoneNumber()) && Objects.equals(getNotes(), customer.getNotes()) && Objects.equals(getPets(), customer.getPets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPhoneNumber(), getNotes(), getPets());
    }

    public static Customer EMPTY = of();


}
