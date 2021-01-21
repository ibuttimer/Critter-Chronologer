package com.udacity.jdnd.course3.critter.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.exception.InvalidScheduleException;
import com.udacity.jdnd.course3.critter.schedule.Schedule;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class ScheduleService extends AbstractService<Schedule> {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    PetService petService;

    private ScheduleRepository scheduleRepository;

    public ScheduleService(AbstractRepository<Schedule> repository) {
        super(repository);
        scheduleRepository = (ScheduleRepository) repository;
    }

    public List<Schedule> getScheduleForPet(long petId) {
        return scheduleRepository.findScheduleByPet(petId);
    }

    public List<Schedule> getScheduleForEmployee(long employeeId) {
        return scheduleRepository.findScheduleByEmployee(employeeId);
    }

    public List<Schedule> getScheduleForCustomer(long customerId) {
        return scheduleRepository.findScheduleByCustomer(customerId);
    }

    @Override
    protected Schedule getEntityInstance() {
        return new Schedule();
    }

    /**
     * Validate an input entity
     * Validation rules
     *  - must have at least one employee
     *  - must have at least one pet
     *  - valid date
     *  - must have at least one activity
     * The supplied ids are verified in {@link AbstractRepository#find(List)}.
     * @param entity - entity to validate
     * @return
     */
    @Override
    protected Schedule validateInputEntity(Schedule entity) {
        List<String> errors = Lists.newArrayList();
        if (entity.getEmployees() == null || entity.getEmployees().size() == 0) {
            errors.add("No employees specified");
        }
        if (entity.getPets() == null || entity.getPets().size() == 0) {
            errors.add("No pets specified");
        }
        if (entity.getDate() == null) {
            errors.add("No date specified");
        }
        if (entity.getActivities() == null || entity.getActivities().size() == 0) {
            errors.add("No activities specified");
        }
        if (errors.size() > 0) {
            throw new InvalidScheduleException(Joiner.on("\n").join(errors));
        }
        return entity;
    }

    /**
     * Validate an output entity
     * Validation rules
     *  - required activities can be performed by employees
     * @param entity - entity to validate
     * @return
     */
    @Override
    protected Schedule validateOutputEntity(Schedule entity) {
        List<String> errors = Lists.newArrayList();
        Set<EmployeeSkill> employeeSkills = entity.getEmployees().stream()
                    .flatMap(e -> e.getSkills().stream())
                    .collect(Collectors.toSet());
        Set<EmployeeSkill> intersection = new HashSet<>(Sets.intersection(employeeSkills, entity.getActivities()));
        if (!intersection.equals(entity.getActivities())) {
            errors.add("Not all required activities can be carried out");
        }
        if (errors.size() > 0) {
            throw new InvalidScheduleException(Joiner.on("\n").join(errors));
        }
        return entity;
    }

    @Override
    protected Schedule convertProxies(Schedule entity) {
        // convert proxies for employees & pets
        return convertProxies(entity, getProxyIds(entity.getEmployees()), getProxyIds(entity.getPets()));
    }

    private Schedule convertProxies(Schedule entity, List<Long> employeeIds, List<Long> petIds) {
        // convert ids to employees
        entity.setEmployees(
            employeeService.getInternal(employeeIds)
        );
        // convert ids to pets
        entity.setPets(
            petService.getInternal(petIds)
        );
        return entity;
    }
}
