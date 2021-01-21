package com.udacity.jdnd.course3.critter.user;

import java.time.LocalDate;
import java.util.Set;

/**
 * Represents a request to find available employees by skills.
 */
public class EmployeeRequest {
    private Set<EmployeeSkill> skills;
    private LocalDate date;

    public EmployeeRequest() {
    }

    public EmployeeRequest(Set<EmployeeSkill> skills, LocalDate date) {
        this.skills = skills;
        this.date = date;
    }

    public static EmployeeRequest of(Set<EmployeeSkill> skills, LocalDate date) {
        return new EmployeeRequest(skills, date);
    }

    public Set<EmployeeSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<EmployeeSkill> skills) {
        this.skills = skills;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
