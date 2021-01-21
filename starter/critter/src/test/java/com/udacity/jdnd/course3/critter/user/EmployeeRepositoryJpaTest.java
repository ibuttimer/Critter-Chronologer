package com.udacity.jdnd.course3.critter.user;

import com.google.common.collect.Maps;
import com.udacity.jdnd.course3.critter.matcher.EmployeeField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(EmployeeRepository.class)
public
class EmployeeRepositoryJpaTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    EmployeeRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.clear();
        assertEquals(0, repository.findAll().size());
    }

    @DisplayName("Save employee")
    @Test
    void saveEmployee() {
        saveEmployee(
                getEveryDaySkill());
    }

    private Employee saveEmployee(Employee toSave) {
        return saveEmployee(toSave, testEntityManager, repository);
    }

    private Employee getByName(String name) {
        return getByName(name, repository);
    }

    public static Employee saveEmployee(Employee toSave, TestEntityManager testEntityManager, EmployeeRepository repository) {
        testEntityManager.persist(toSave);

        Employee created = getByName(toSave.getName(), repository);
        assertEmployee(toSave, created, EmployeeField.ID);  // exclude id

        return created;
    }

    public static Employee getByName(String name, EmployeeRepository repository) {
        List<Employee> all = repository.findByName(name);
        assertEquals(1, all.size());
        return all.get(0);
    }

    @DisplayName("Find employee by name/id/skill(s)/day(s)")
    @Test
    void findEmployee() {
        final String everyKey = "every";
        final String mondayKey = "monday";
        Map<String, Employee> employees = Maps.newHashMap();
        List<Employee> all;
        Employee employee;

        // save employees
        for (Employee toSave : List.of(getEveryDaySkill(), getMondayPetting())) {
            Employee created = saveEmployee(toSave);

            employee = repository.findById(created.getId());
            assertEmployee(created, toSave);

            employees.put(employee.getSkills().size() == 1 ? mondayKey : everyKey, employee);
        }

        // find employees with common skill
        all = repository.findBySkill(EmployeeSkill.PETTING);
        assertEquals(2, all.size());    // both employees
        List<Employee> finalPetting = all;
        employees.values()
                .forEach(e -> {
                    assertTrue(finalPetting.contains(e));
                });

        // find employee with unique skill
        all = repository.findBySkill(EmployeeSkill.MEDICATING);
        assertEquals(1, all.size());    // only every day/thing employee
        employee = all.get(0);
        assertEmployee(employees.get(everyKey), employee);

        // find employee with multiple skills
        all = repository.findBySkills(List.of(EmployeeSkill.FEEDING, EmployeeSkill.MEDICATING));
        assertEquals(1, all.size());    // only every day/thing employee
        employee = all.get(0);
        assertEmployee(employees.get(everyKey), employee);

        // find employees available common day
        all = repository.findByDaysAvailable(DayOfWeek.MONDAY);
        assertEquals(2, all.size());    // both employees
        List<Employee> finalAllMonday = all;
        employees.values()
                .forEach(e -> {
                    assertTrue(finalAllMonday.contains(e));
                });

        // find employee available unique day
        all = repository.findByDaysAvailable(DayOfWeek.TUESDAY);
        assertEquals(1, all.size());    // only every day/thing employee
        employee = all.get(0);
        assertEmployee(employees.get(everyKey), employee);

        // find employee available multiple days
        all = repository.findByDaysAvailable(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        assertEquals(1, all.size());    // only every day/thing employee
        employee = all.get(0);
        assertEmployee(employees.get(everyKey), employee);
    }

    @DisplayName("Update employee name/skills/days")
    @Test
    void updateEmployee() {
        Employee created = saveEmployee(getMondayPetting());

        final String newName = "New Name";
        created.setName(newName);

        // update name
        Employee updated = getByName(newName);
        assertEquals(created, updated);

        // update skills
        Set<EmployeeSkill> newSkills = Set.of(EmployeeSkill.FEEDING, EmployeeSkill.WALKING);
        created.setSkills(newSkills);
        List<Employee> all = repository.findBySkills(newSkills);
        assertEquals(1, all.size());
        updated = all.get(0);
        assertEquals(created, updated);

        all = repository.findBySkill(EmployeeSkill.MEDICATING);
        assertEquals(0, all.size());

        // update days
        Set<DayOfWeek> newDays = Set.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
        created.setDaysAvailable(newDays);
        all = repository.findByDaysAvailable(newDays);
        assertEquals(1, all.size());
        updated = all.get(0);
        assertEquals(created, updated);

        all = repository.findByDaysAvailable(DayOfWeek.THURSDAY);
        assertEquals(0, all.size());
    }

    @DisplayName("Delete employee")
    @Test
    void deleteEmployee() {
        Employee created = saveEmployee(getMondayPetting());

        int affected = repository.delete(created.getId());
        assertEquals(1, affected);
        assertEquals(0, repository.findAll().size());
    }

    static void assertEmployee(Employee expected, Employee actual, EmployeeField...exclude) {
        for (EmployeeField field : EmployeeField.values()) {
            if (!Arrays.asList(exclude).contains(field)) {
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId());
                        break;
                    case NAME:
                        assertEquals(expected.getName(), actual.getName());
                        break;
                    case SKILLS:
                        assertEquals(expected.getSkills(), actual.getSkills());
                        break;
                    case DAYS_AVAILABLE:
                        assertEquals(expected.getDaysAvailable(), actual.getDaysAvailable());
                        break;
                }
            }
        }
    }


    public static Employee getEveryDaySkill() {
        final String name = "Every Day-Skill";
        return Employee.of(0, name, Set.of(EmployeeSkill.values()), Set.of(DayOfWeek.values()));
    }

    public static Employee getMondayPetting() {
        final String name = "Monday Petting";
        return Employee.of(0, name, Set.of(EmployeeSkill.PETTING), Set.of(DayOfWeek.MONDAY));
    }

}