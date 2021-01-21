package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.common.Creature_;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.DayOfWeek;

@StaticMetamodel(Employee.class)
public class Employee_ extends Creature_ {

    public static volatile SetAttribute<Employee, EmployeeSkill> skills;
    public static volatile SetAttribute<Employee, DayOfWeek> daysAvailable;
}
