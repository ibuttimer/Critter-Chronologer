package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.common.Views;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.schedule.Schedule;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.user.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.schedule.Schedule.*;
import static com.udacity.jdnd.course3.critter.user.CustomerDTO.PET_IDS_COL;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping(SCHEDULE_URL)
public class ScheduleController extends AbstractController<Schedule, ScheduleDTO> {

    @Autowired
    ScheduleService scheduleService;

    @PostConstruct
    void init() {
        setService(scheduleService);
    }

    @JsonView(Views.Public.class)
    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        return save(scheduleDTO);
    }

    @JsonView(Views.Public.class)
    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return getAll();
    }

    @JsonView(Views.Public.class)
    @GetMapping(GET_SCHEDULE_BY_ID_URL)
    public ScheduleDTO getScheduleById(@PathVariable long scheduleId) {
        return convertEntityToDto(
                scheduleService.get(scheduleId));
    }

    @JsonView(Views.Public.class)
    @GetMapping(GET_SCHEDULE_BY_PET_URL)
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return convertEntityToDto(
                scheduleService.getScheduleForPet(petId));
    }

    @JsonView(Views.Public.class)
    @GetMapping(GET_SCHEDULE_BY_EMPLOYEE_URL)
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return convertEntityToDto(
                scheduleService.getScheduleForEmployee(employeeId));
    }

    @JsonView(Views.Public.class)
    @GetMapping(GET_SCHEDULE_BY_CUSTOMER_URL)
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        return convertEntityToDto(
                scheduleService.getScheduleForCustomer(customerId));
    }

    @JsonView(Views.Public.class)
    @GetMapping(GET_SCHEDULE_BY_OWNER_URL)
    public List<ScheduleDTO> getScheduleForOwner(@PathVariable long ownerId) {
        return getScheduleForCustomer(ownerId);
    }


    @Override
    protected Schedule getEntityInstance() {
        return new Schedule();
    }

    @Override
    protected ScheduleDTO getDtoInstance() {
        return new ScheduleDTO();
    }

    @Override
    public Schedule convertDtoToEntity(ScheduleDTO dto) {
        Schedule entity = super.convertDtoToEntity(dto);
        // convert ids to employee proxies
        entity.setEmployees(
            dto.getEmployeeIds().stream()
                .map(Employee::proxy)
                .collect(Collectors.toList())
        );
        // convert ids to pet proxies
        entity.setPets(
            dto.getPetIds().stream()
                .map(Pet::proxy)
                .collect(Collectors.toList())
        );
        return entity;
    }

    @Override
    public ScheduleDTO convertEntityToDto(Schedule entity) {
        ScheduleDTO dto = super.convertEntityToDto(entity);
        // convert employees to ids
        dto.setEmployeeIds(
            makeProxyIds(entity.getEmployees())
        );
        // convert pets to ids
        dto.setPetIds(
            makeProxyIds(entity.getPets())
        );
        return dto;
    }

    @Override
    protected Schedule copyUpdateProperties(ScheduleDTO dto, Schedule entity) {
        super.copyUpdateProperties(dto, entity);
        return dtoToEntityTranslate(dto, entity);
    }

    protected Schedule dtoToEntityTranslate(ScheduleDTO dto, Schedule entity) {
        // convert employee ids to pet proxies
        if (dto.getEmployeeIds() != null) {
            entity.setEmployees(
                dto.getEmployeeIds().stream()
                    .map(Employee::proxy)
                    .collect(Collectors.toList())
            );
        }
        // convert pet ids to pet proxies
        if (dto.getPetIds() != null) {
            entity.setPets(
                dto.getPetIds().stream()
                    .map(Pet::proxy)
                    .collect(Collectors.toList())
            );
        }
        return entity;
    }

    @Override
    public List<String> getUpdatePropertiesToIgnore(ScheduleDTO dto) {
        List<String> list = Lists.newArrayList(super.getUpdatePropertiesToIgnore(dto));
        if (dto.getPetIds() == null) {
            list.add(PET_IDS_COL);
        }
        if (dto.getEmployeeIds() == null) {
            list.add(EMPLOYEES_COL);
        }
        if (dto.getDate() == null) {
            list.add(DATE_COL);
        }
        if (dto.getActivities() == null) {
            list.add(ACTIVITIES_COL);
        }
        return list;
    }

}
