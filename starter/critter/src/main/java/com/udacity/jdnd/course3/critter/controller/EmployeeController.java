package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.udacity.jdnd.course3.critter.common.Views;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.user.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static com.udacity.jdnd.course3.critter.config.Config.*;

/**
 * Handles web requests related to Employees.
 */
@RestController
@RequestMapping(USER_URL)
public class EmployeeController extends AbstractController<Employee, EmployeeDTO> {

    @Autowired
    EmployeeService employeeService;

    @PostConstruct
    void init() {
        setService(employeeService);
    }

    @JsonView(Views.Public.class)
    @PostMapping(EMPLOYEE_POST_URL)
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return save(employeeDTO);
    }

    @JsonView(Views.Public.class)
    @GetMapping(EMPLOYEE_GET_BY_ID_URL)
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        return get(employeeId);
    }

    @JsonView(Views.Public.class)
    @PutMapping(EMPLOYEE_PUT_BY_ID_URL)
    public EmployeeDTO setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        return convertEntityToDto(
                employeeService.setAvailability(daysAvailable, employeeId));
    }

    @JsonView(Views.Public.class)
    @GetMapping(EMPLOYEE_AVAILABILITY_URL)
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return convertEntityToDto(
                employeeService.findEmployeesForService(
                        convertEmployeeRequestDtoToEntity(employeeRequestDTO)));
    }

    @Override
    protected Employee getEntityInstance() {
        return new Employee();
    }

    @Override
    protected EmployeeDTO getDtoInstance() {
        return new EmployeeDTO();
    }

    protected EmployeeRequest convertEmployeeRequestDtoToEntity(EmployeeRequestDTO employeeRequestDTO) {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        BeanUtils.copyProperties(employeeRequestDTO, employeeRequest);
        return employeeRequest;
    }
}
