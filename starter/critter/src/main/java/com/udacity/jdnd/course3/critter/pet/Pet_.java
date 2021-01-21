package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.common.Creature_;
import com.udacity.jdnd.course3.critter.user.Customer;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;

@StaticMetamodel(Pet.class)
public class Pet_ extends Creature_ {

    public static volatile SingularAttribute<Pet, PetType> type;
    public static volatile SingularAttribute<Pet, Customer> owner;
    public static volatile SingularAttribute<Pet, LocalDate> birthDate;
    public static volatile SingularAttribute<Pet, String> notes;
}
