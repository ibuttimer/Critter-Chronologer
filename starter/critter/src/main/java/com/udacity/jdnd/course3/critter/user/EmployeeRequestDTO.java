package com.udacity.jdnd.course3.critter.user;

import java.time.LocalDate;
import java.util.Set;

/**
 * Represents a request to find available employees by skills. Does not map
 * to the database directly.
 */
public class EmployeeRequestDTO {
    private Set<EmployeeSkill> skills;
    private LocalDate date;

    public EmployeeRequestDTO() {
    }

    public EmployeeRequestDTO(Set<EmployeeSkill> skills, LocalDate date) {
        this.skills = skills;
        this.date = date;
    }

    public static EmployeeRequestDTO of(Set<EmployeeSkill> skills, LocalDate date) {
        return new EmployeeRequestDTO(skills, date);
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
