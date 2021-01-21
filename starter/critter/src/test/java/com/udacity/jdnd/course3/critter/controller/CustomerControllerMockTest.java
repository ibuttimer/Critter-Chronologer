package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.udacity.jdnd.course3.critter.config.Config.CUSTOMER_POST_URL;
import static com.udacity.jdnd.course3.critter.controller.CustomerControllerTest.getCustomerUri;
import static com.udacity.jdnd.course3.critter.matcher.CustomerJsonPathResultMatchers.customerJsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerMockTest extends AbstractTest {

    @MockBean
    CustomerService customerService;

    ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("Create new customer")
    @Test
    public void createCustomer() throws Exception {

        Customer entity = Customer.of(
            1, "New Customer", "123456789", "Interesting note", List.of()
        );
        CustomerDTO dto = CustomerDTO.of(
                entity.getId(), entity.getName(), entity.getPhoneNumber(), entity.getNotes(), List.of()
        );

        given(customerService.save(any())).willReturn(entity);

        mockMvc.perform(
                post(new URI(
                            getCustomerUri(List.of(CUSTOMER_POST_URL), Map.of())))
                        .content(objectMapper.writeValueAsString(entity))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(customerJsonPath("$").value(dto));
    }
}