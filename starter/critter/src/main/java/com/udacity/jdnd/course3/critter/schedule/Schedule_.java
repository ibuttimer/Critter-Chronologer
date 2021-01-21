package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;

@StaticMetamodel(Schedule.class)
public class Schedule_ {

    public static volatile SingularAttribute<Schedule, Long> id;
    public static volatile ListAttribute<Schedule, Employee> employees;
    public static volatile ListAttribute<Schedule, Pet> pets;
    public static volatile SingularAttribute<Schedule, LocalDate> date;
    public static volatile ListAttribute<Schedule, EmployeeSkill> activities;
}
