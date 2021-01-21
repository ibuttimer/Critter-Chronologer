package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.common.Creature_;
import com.udacity.jdnd.course3.critter.pet.Pet;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Customer.class)
public class Customer_ extends Creature_ {

    public static volatile SingularAttribute<Customer, String> phoneNumber;
    public static volatile SingularAttribute<Customer, String> notes;
    public static volatile ListAttribute<Customer, Pet> pets;
}
