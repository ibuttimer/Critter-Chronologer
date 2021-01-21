package com.udacity.jdnd.course3.critter.service;

import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.exception.InvalidEmployeeException;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Service
@Validated
public class EmployeeService extends AbstractService<Employee> {

    EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository repository) {
        super(repository);
        this.employeeRepository = repository;
    }

    public Employee setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        /* as employee skills are stored in a separate collection table (DAYS_AVAILABLE_TABLE), a CriteriaUpdate
            won't work as 'Attribute path for assignment must represent a singular attribute'
         */
        Employee employee = getInternal(employeeId);
        employee.setDaysAvailable(daysAvailable);
        return employee;
    }

    public List<Employee> findEmployeesForService(EmployeeRequest employeeRequest) {
        return ((EmployeeRepository)repository).findEmployeesForService(employeeRequest);
    }

    @Override
    protected Employee getEntityInstance() {
        return new Employee();
    }

    /**
     * Validate an entity
     * Validation rules
     *  - must have name
     * @param entity - entity to validate
     * @return
     */
    @Override
    protected Employee validateInputEntity(Employee entity) {
        List<String> errors = Lists.newArrayList();
        if (!StringUtils.hasText(entity.getName())) {
            errors.add("Name required");
        }
        if (entity.getSkills() == null) {
            entity.setSkills(Set.of());
        }
        if (entity.getDaysAvailable() == null) {
            entity.setDaysAvailable(Set.of());
        }
        if (errors.size() > 0) {
            throw new InvalidEmployeeException(errors);
        }
        return entity;
    }

    @Override
    protected Employee validateOutputEntity(Employee entity) {
        return entity;
    }

    @Override
    protected Employee convertProxies(Employee entity) {
        // nothing to do
        return entity;
    }
}
