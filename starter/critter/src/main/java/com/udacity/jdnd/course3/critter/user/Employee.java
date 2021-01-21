package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.common.Creature;
import com.udacity.jdnd.course3.critter.common.IEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.Objects;
import java.util.Set;

@Entity
public class Employee extends Creature implements IEntity {

    public static final String SKILLS_COL = "skills";
    public static final String DAYS_AVAILABLE_COL = "daysAvailable";

    /** Name of table to store employee skills */
    public static final String EMPLOYEE_SKILLS_TABLE = "employee_skills";
    /** Name of table to store employee days available */
    public static final String DAYS_AVAILABLE_TABLE = "employee_days_available";


    @NotNull
    @ElementCollection(targetClass = EmployeeSkill.class)
    @CollectionTable(
            name = EMPLOYEE_SKILLS_TABLE,
            joinColumns = @JoinColumn(name = "employee_id")
    )
    @JoinColumn(name = "employee_id")            // name of the @Id column of this entity
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Enumerated(EnumType.STRING)
    private Set<EmployeeSkill> skills;

    @NotNull
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(
            name = DAYS_AVAILABLE_TABLE,
            joinColumns = @JoinColumn(name = "employee_id")
    )
    @JoinColumn(name = "employee_id")            // name of the @Id column of this entity
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> daysAvailable;

    public Employee() {
        super();
    }

    public Employee(long id, String name, Set<EmployeeSkill> skills, Set<DayOfWeek> daysAvailable) {
        super(id, name);
        init(skills, daysAvailable);
    }

    private void init(Set<EmployeeSkill> skills, Set<DayOfWeek> daysAvailable) {
        this.skills = skills;
        this.daysAvailable = daysAvailable;
    }

    public static Employee of() {
        return new Employee();
    }

    public static Employee proxy(long id) {
        Employee employee = new Employee();
        employee.proxy = true;
        employee.setId(id);
        return employee;
    }

    public static Employee of(long id, String name, Set<EmployeeSkill> skills, Set<DayOfWeek> daysAvailable) {
        return new Employee(id, name, skills, daysAvailable);
    }

    public Set<EmployeeSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<EmployeeSkill> skills) {
        this.skills = skills;
    }

    public Set<DayOfWeek> getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(Set<DayOfWeek> daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    @Override
    public String toString() {
        return super.toString().replace('}', ',') +
                " skills=" + skills +
                ", daysAvailable=" + daysAvailable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getSkills(), employee.getSkills()) && Objects.equals(getDaysAvailable(), employee.getDaysAvailable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSkills(), getDaysAvailable());
    }
}
