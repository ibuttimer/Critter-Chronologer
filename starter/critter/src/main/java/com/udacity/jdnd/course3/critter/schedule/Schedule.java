package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.common.IEntity;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Schedule implements IEntity {

    public static final String EMPLOYEES_COL = "employees";
    public static final String PETS_COL = "pets";
    public static final String DATE_COL = "date";
    public static final String ACTIVITIES_COL = "activities";

    @Id
    @GeneratedValue
    private long id;

    @ManyToMany
    @JoinTable(
            name = "schedule_employee",
            joinColumns = { @JoinColumn(name = "schedule_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id")}
    )
    private List<Employee> employees;

    @ManyToMany
    @JoinTable(
            name = "schedule_pet",
            joinColumns = { @JoinColumn(name = "schedule_id")},
            inverseJoinColumns = { @JoinColumn(name = "pet_id")}
    )
    private List<Pet> pets;

    @NotNull
    private LocalDate date;

    @NotNull
    @ElementCollection(targetClass = EmployeeSkill.class)
    @CollectionTable(
            name = "schedule_activities",
            joinColumns = @JoinColumn(name = "schedule_id")
    )
    @JoinColumn(name = "schedule_id")            // name of the @Id column of this entity
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Enumerated(EnumType.STRING)
    private Set<EmployeeSkill> activities;

    @Transient
    boolean proxy;

    public Schedule() {
        proxy = false;
    }

    public Schedule(long id, List<Employee> employees, List<Pet> pets, @NotNull LocalDate date, @NotNull Set<EmployeeSkill> activities) {
        this();
        this.id = id;
        this.employees = employees;
        this.pets = pets;
        this.date = date;
        this.activities = activities;
    }

    public static Schedule of(long id, List<Employee> employees, List<Pet> pets, @NotNull LocalDate date, @NotNull Set<EmployeeSkill> activities) {
        return new Schedule(id, employees, pets, date, activities);
    }

    public static Schedule proxy(long id) {
        Schedule schedule = new Schedule();
        schedule.proxy = true;
        schedule.setId(id);
        return schedule;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<EmployeeSkill> getActivities() {
        return activities;
    }

    public void setActivities(Set<EmployeeSkill> activities) {
        this.activities = activities;
    }

    @Override
    public boolean isProxy() {
        return proxy;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", proxy=" + proxy +
                ", employees=" + employees +
                ", pets=" + pets +
                ", date=" + date +
                ", activities=" + activities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;

        Schedule schedule = (Schedule) o;

        if (getId() != schedule.getId()) return false;
        if (isProxy() != schedule.isProxy()) return false;
        if (getEmployees() != null ? !getEmployees().equals(schedule.getEmployees()) : schedule.getEmployees() != null)
            return false;
        if (getPets() != null ? !getPets().equals(schedule.getPets()) : schedule.getPets() != null) return false;
        if (getDate() != null ? !getDate().equals(schedule.getDate()) : schedule.getDate() != null) return false;
        return getActivities() != null ? getActivities().equals(schedule.getActivities()) : schedule.getActivities() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getEmployees() != null ? getEmployees().hashCode() : 0);
        result = 31 * result + (getPets() != null ? getPets().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getActivities() != null ? getActivities().hashCode() : 0);
        result = 31 * result + (isProxy() ? 1 : 0);
        return result;
    }
}
