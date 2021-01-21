package com.udacity.jdnd.course3.critter.schedule;

import com.fasterxml.jackson.annotation.JsonView;
import com.udacity.jdnd.course3.critter.common.IDto;
import com.udacity.jdnd.course3.critter.common.Views;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Represents the form that schedule request and response data takes. Does not map
 * to the database directly.
 */
public class ScheduleDTO implements IDto {

    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private List<Long> employeeIds;

    @JsonView(Views.Public.class)
    private List<Long> petIds;

    @JsonView(Views.Public.class)
    private LocalDate date;

    @JsonView(Views.Public.class)
    private Set<EmployeeSkill> activities;

    public ScheduleDTO() {
    }

    public ScheduleDTO(long id, List<Long> employeeIds, List<Long> petIds, LocalDate date, Set<EmployeeSkill> activities) {
        this.id = id;
        this.employeeIds = employeeIds;
        this.petIds = petIds;
        this.date = date;
        this.activities = activities;
    }

    public static ScheduleDTO of(long id, List<Long> employeeIds, List<Long> petIds, LocalDate date, Set<EmployeeSkill> activities) {
        return new ScheduleDTO(id, employeeIds, petIds, date, activities);
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

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<Long> getPetIds() {
        return petIds;
    }

    public void setPetIds(List<Long> petIds) {
        this.petIds = petIds;
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
    public String toString() {
        return "ScheduleDTO{" +
                "id=" + id +
                ", employeeIds=" + employeeIds +
                ", petIds=" + petIds +
                ", date=" + date +
                ", activities=" + activities +
                '}';
    }
}
