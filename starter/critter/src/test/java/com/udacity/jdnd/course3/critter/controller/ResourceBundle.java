package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import org.springframework.test.web.servlet.MockMvc;

public class ResourceBundle {

    MockMvc mockMvc;

    ScheduleRepository scheduleRepository;

    PetRepository petRepository;

    CustomerRepository customerRepository;

    EmployeeRepository employeeRepository;

    public static ResourceBundle of() {
        return new ResourceBundle();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public ResourceBundle setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        return this;
    }

    public ScheduleRepository getScheduleRepository() {
        return scheduleRepository;
    }

    public ResourceBundle setScheduleRepository(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
        return this;
    }

    public PetRepository getPetRepository() {
        return petRepository;
    }

    public ResourceBundle setPetRepository(PetRepository petRepository) {
        this.petRepository = petRepository;
        return this;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public ResourceBundle setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        return this;
    }

    public EmployeeRepository getEmployeeRepository() {
        return employeeRepository;
    }

    public ResourceBundle setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        return this;
    }
}
