package com.udacity.jdnd.course3.critter.pet;

import com.fasterxml.jackson.annotation.JsonView;
import com.udacity.jdnd.course3.critter.common.IDto;
import com.udacity.jdnd.course3.critter.common.Views;

import java.time.LocalDate;

/**
 * Represents the form that pet request and response data takes. Does not map
 * to the database directly.
 */
public class PetDTO implements IDto {

    public static final String OWNER_ID_COL = "ownerId";

    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private PetType type;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    private long ownerId;

    @JsonView(Views.Public.class)
    private LocalDate birthDate;

    @JsonView(Views.Public.class)
    private String notes;

    public PetDTO() {
    }

    public PetDTO(long id, PetType type, String name, long ownerId, LocalDate birthDate, String notes) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.ownerId = ownerId;
        this.birthDate = birthDate;
        this.notes = notes;
    }

    public static PetDTO of(long id, PetType type, String name, long ownerId, LocalDate birthDate, String notes) {
        return new PetDTO(id, type, name, ownerId, birthDate, notes);
    }

    @JsonView(Views.Internal.class)
    @Override
    public boolean isProxy() {
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
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
        return "PetDTO{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", birthDate=" + birthDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
