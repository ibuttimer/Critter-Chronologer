package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.common.Creature;
import com.udacity.jdnd.course3.critter.common.IEntity;
import com.udacity.jdnd.course3.critter.user.Customer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Pet extends Creature implements IEntity {

    public static final String TYPE_COL = "type";
    public static final String OWNER_COL = "owner";
    public static final String BIRTH_DATE_COL = "birthDate";
    public static final String NOTES_COL = "notes";

    @NotNull
    @Enumerated(EnumType.STRING)
    private PetType type;

    @ManyToOne
    @JoinColumn(name ="owner_id")
    private Customer owner;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private String notes;

    public Pet() {
        super();
    }

    public Pet(long id, String name, PetType type, Customer owner, LocalDate birthDate, String notes) {
        super(id, name);
        init(type, owner, birthDate, notes);
    }

    private void init(PetType type, Customer owner, LocalDate birthDate, String notes) {
        this.type = type;
        this.owner = owner;
        this.birthDate = birthDate;
        this.notes = notes;
    }

    public static Pet of() {
        return new Pet();
    }

    public static Pet proxy(long id) {
        Pet pet = new Pet();
        pet.proxy = true;
        pet.setId(id);
        return pet;
    }

    public static Pet of(long id, String name, PetType type, Customer owner, LocalDate birthDate, String notes) {
        return new Pet(id, name, type, owner, birthDate, notes);
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        if (!Objects.equals(this.owner, owner)) {
            // set new owner
            Customer oldOwner = this.owner;
            this.owner = owner;
            // remove from the old owner
            if (oldOwner != null) {
                oldOwner.removePet(this);
            }
            // add to new owner
            if (owner != null)
                owner.addPet(this);
        }
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return super.toString().replace('}', ',') +
                " type=" + type +
                ", owner=" + owner +
                ", birthDate=" + birthDate +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet)) return false;
        if (!super.equals(o)) return false;
        Pet pet = (Pet) o;
        return getType() == pet.getType() && Objects.equals(getOwner(), pet.getOwner())
                && Objects.equals(getBirthDate(), pet.getBirthDate()) && Objects.equals(getNotes(), pet.getNotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getType(), getOwner(), getBirthDate(), getNotes());
    }


}
