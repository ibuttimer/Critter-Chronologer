package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.matcher.ScheduleField;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.user.CustomerRepositoryJpaTest.saveCustomer;
import static com.udacity.jdnd.course3.critter.user.EmployeeRepositoryJpaTest.saveEmployee;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({
    ScheduleRepository.class, EmployeeRepository.class, CustomerRepository.class
})
class ScheduleRepositoryJpaTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    ScheduleRepository repository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void beforeEach() {
        for (AbstractRepository<?> repo : List.of(repository, employeeRepository, customerRepository)) {
            repository.clear();
            assertEquals(0, repository.findAll().size());
        }
    }

    @DisplayName("Save schedule")
    @Test
    void saveSchedule() {
        Employee toSaveEmployee = EmployeeRepositoryJpaTest.getEveryDaySkill();
        saveSchedule(CustomerRepositoryJpaTest.getMultiplePetsCustomer(), List.of(
                toSaveEmployee
        ), LocalDate.now(), toSaveEmployee.getSkills(), 1);
    }

    Schedule saveSchedule(Customer customerToSave, List<Employee> employeesToSave, LocalDate date, Set<EmployeeSkill> activities,
                      int expectedCount) {
        Customer customer = saveCustomer(customerToSave, testEntityManager, customerRepository);
        // flush to avoid 'object references an unsaved transient instance' exception
        customerRepository.flush();
        List<Employee> employees = employeesToSave.stream()
                .map(employee -> saveEmployee(employee, testEntityManager, employeeRepository))
                .collect(Collectors.toList());
        employeeRepository.flush();
        // use copies to avoid 'shared references to a collection' persistence exceptions
        Schedule toSave = Schedule.of(0, employees, List.copyOf(customer.getPets()), date,
                Set.copyOf(activities));

        testEntityManager.persist(toSave);

        List<Schedule> all = repository.findAll();
        assertEquals(expectedCount, all.size());

        Schedule created = all.get(expectedCount - 1);
        assertSchedule(toSave, created, ScheduleField.ID);  // exclude id

        return created;
    }

    @DisplayName("Find schedule")
    @Test
    void findSchedule() {
        int expectedCount = 0;
        Employee toSaveEmployee = EmployeeRepositoryJpaTest.getEveryDaySkill();
        Schedule created = saveSchedule(
                CustomerRepositoryJpaTest.getMultiplePetsCustomer(), List.of(
                        toSaveEmployee
                ), LocalDate.now(), toSaveEmployee.getSkills(), ++expectedCount);
        toSaveEmployee = EmployeeRepositoryJpaTest.getMondayPetting();
        saveSchedule(
                CustomerRepositoryJpaTest.getNoPetsCustomer(), List.of(
                        toSaveEmployee
                ), LocalDate.now().plusDays(2), toSaveEmployee.getSkills(), ++expectedCount);

        Schedule schedule = repository.findById(created.getId());
        assertSchedule(created, schedule);

        List<Schedule> schedules = repository.findByDate(created.getDate());
        assertEquals(1, schedules.size());
        assertSchedule(created, schedules.get(0));
    }

    @DisplayName("Update schedule date/employee/pets/activities")
    @Test
    void updateSchedule() {
        Employee toSaveEmployee = EmployeeRepositoryJpaTest.getEveryDaySkill();
        Schedule created = saveSchedule(
                CustomerRepositoryJpaTest.getMultiplePetsCustomer(), List.of(
                        toSaveEmployee,
                        EmployeeRepositoryJpaTest.getMondayPetting()
                ), LocalDate.now(), toSaveEmployee.getSkills(), 1);

        // update date
        LocalDate newDate = created.getDate().plusDays(2);
        created.setDate(newDate);
        assertEquals(newDate, created.getDate());

        Schedule schedule = repository.findById(created.getId());
        assertSchedule(created, schedule);

        // update employees
        List<Employee> oldEmployees = created.getEmployees();
        List<Employee> newEmployees = List.copyOf(
                oldEmployees.subList(0, oldEmployees.size() / 2)
        );
        created.setEmployees(newEmployees);
        assertEquals(newEmployees, created.getEmployees());

        schedule = repository.findById(created.getId());
        assertSchedule(created, schedule);

        // update pets
        List<Pet> oldPets = created.getPets();
        List<Pet> newPets = List.copyOf(
                oldPets.subList(0, oldPets.size() / 2)
        );
        created.setPets(newPets);
        assertEquals(newPets, created.getPets());

        schedule = repository.findById(created.getId());
        assertSchedule(created, schedule);

        // update activities
        Set<EmployeeSkill> oldActivities = created.getActivities();
        Set<EmployeeSkill> newActivities = Set.of(
                List.of(oldActivities.toArray(EmployeeSkill[]::new))
                        .subList(0, oldActivities.size() / 2)
                        .toArray(EmployeeSkill[]::new)
        );
        created.setActivities(newActivities);
        assertEquals(newActivities, created.getActivities());

        schedule = repository.findById(created.getId());
        assertSchedule(created, schedule);
    }

    @DisplayName("Delete schedule")
    @Test
    void deleteEmployee() {
        Employee toSaveEmployee = EmployeeRepositoryJpaTest.getEveryDaySkill();
        Schedule created = saveSchedule(
                CustomerRepositoryJpaTest.getMultiplePetsCustomer(), List.of(
                        toSaveEmployee,
                        EmployeeRepositoryJpaTest.getMondayPetting()
                ), LocalDate.now(), toSaveEmployee.getSkills(), 1);

        int affected = repository.delete(created.getId());
        assertEquals(1, affected);
        assertEquals(0, repository.findAll().size());
    }

    void assertSchedule(Schedule expected, Schedule actual, ScheduleField...excluded) {
        for (ScheduleField field : ScheduleField.values()) {
            if (!Arrays.asList(excluded).contains(field)) {
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId());
                        break;
                    case EMPLOYEES:
                        assertEquals(expected.getEmployees(), actual.getEmployees());
                        break;
                    case PETS:
                        assertEquals(expected.getPets(), actual.getPets());
                        break;
                    case DATE:
                        assertEquals(expected.getDate(), actual.getDate());
                        break;
                    case ACTIVITIES:
                        assertEquals(expected.getActivities(), actual.getActivities());
                        break;
                }
            }

        }
    }

}