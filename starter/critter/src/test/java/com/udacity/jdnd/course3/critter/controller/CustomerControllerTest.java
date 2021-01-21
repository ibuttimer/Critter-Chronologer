package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.matcher.CustomerField;
import com.udacity.jdnd.course3.critter.matcher.PetField;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.pet.PetType;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.controller.PetControllerTest.savePet;
import static com.udacity.jdnd.course3.critter.matcher.CustomerJsonPathResultMatchers.customerJsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest extends AbstractContextTest {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PetRepository petRepository;

    static AtomicInteger customerCount = new AtomicInteger();

    @BeforeEach
    void beforeEach() {
        for (AbstractRepository<?> repository : List.of(customerRepository, petRepository)) {
            repository.clear();
            assertEquals(0, repository.findAll().size());
        }
    }

    @DisplayName("Create new customer")
    @Test
    public void createCustomer() throws Exception {
        saveCustomer(mockMvc, getNoPetsCustomer(), List.of(CustomerField.ID));    // exclude id
    }

    @DisplayName("Cannot create invalid customer")
    @Test
    public void createInvalidCustomer() throws Exception {
        CustomerDTO customer = getNoPetsCustomer();
        customer.setName("");
        saveInvalidCustomer(mockMvc, customer, List.of(CustomerField.ID));    // exclude id

        customer = getNoPetsCustomer();
        customer.setPhoneNumber("");
        saveInvalidCustomer(mockMvc, customer, List.of(CustomerField.ID));    // exclude id

        customer = getNoPetsCustomer();
        customer.setPetIds(List.of(1000L));
        saveInvalidCustomer(mockMvc, customer, List.of(CustomerField.ID));    // exclude id
    }

    @DisplayName("Get customer by id")
    @Test
    public void getCustomerById() throws Exception {

        // save customer
        CustomerDTO customer = saveCustomer(mockMvc, getNoPetsCustomer(), List.of(CustomerField.ID));    // exclude id

        // get all customers and verify
        mockMvc.perform(
                    get(getCustomerUri(List.of(CUSTOMER_GET_URL, CUSTOMER_ID_URL), Map.of(CUSTOMER_ID_PATTERN, customer.getId()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(customerJsonPath(List.of(), "$").value(customer));
    }

    @DisplayName("Get non-existent customer")
    @Test
    public void getInvalidCustomerById() throws Exception {

        // get all customers and verify
        mockMvc.perform(
                    get(getCustomerUri(List.of(CUSTOMER_GET_URL, CUSTOMER_ID_URL), Map.of(CUSTOMER_ID_PATTERN, 1000L))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Add pet to customer")
    @Test
    public void addPetToCustomer() throws Exception {

        // save customer
        CustomerDTO customer = saveCustomer(mockMvc, getNoPetsCustomer(), List.of(CustomerField.ID));    // exclude id

        // save pet as customer's
        PetDTO pet = savePet(mockMvc, PetDTO.of(
                0, PetType.DOG, "Fido", customer.getId(), LocalDate.now(), "Bad dog"),
                List.of(PetField.ID));    // exclude id

        // update customer with what new value should be
        customer.addPetId(pet.getId());

        // get customer and verify
        mockMvc.perform(
                    get(getCustomerUri(List.of(CUSTOMER_GET_ID_URL), Map.of(CUSTOMER_ID_PATTERN, customer.getId()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(customerJsonPath(List.of(), "$").value(customer));
    }

    @DisplayName("Find owner by pet")
    @Test
    public void findOwnerByPet() throws Exception {

        List<CustomerField> excludes = List.of(CustomerField.ID);

        // save multiple customers
        List<CustomerDTO> customers = saveCustomers(mockMvc, List.of(
                getNoPetsCustomer(), getNoPetsCustomer(), getNoPetsCustomer()), excludes);
        CustomerDTO customer = customers.get(customers.size() / 2);

        // save pet as middle customer's
        PetDTO pet = savePet(mockMvc, PetDTO.of(
                0, PetType.DOG, "Fido", customer.getId(), LocalDate.now(), "Bad dog"),
                List.of(PetField.ID));    // exclude id

        // update customer with what new value should be
        customer.addPetId(pet.getId());

        // get owner/customer by pet id and verify
        for (String partUrl : List.of(OWNER_GET_BY_PET_URL, CUSTOMER_GET_BY_PET_URL)) {
            mockMvc.perform(
                    get(getCustomerUri(List.of(partUrl), Map.of(PET_ID_PATTERN, pet.getId()))))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(customerJsonPath(excludes, "$").value(customer));
        }
    }

    @DisplayName("Get all customers")
    @Test
    public void getAllCustomer() throws Exception {

        List<CustomerField> excludes = List.of(CustomerField.ID);

        // save multiple customers
        List<CustomerDTO> customers = saveCustomers(mockMvc, List.of(
                getNoPetsCustomer(), getNoPetsCustomer(), getNoPetsCustomer()), excludes);

        // get all customers and verify
        mockMvc.perform(
                    get(getCustomerUri(List.of(CUSTOMER_GET_URL), Map.of())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(customerJsonPath(excludes, "$").value(customers));
    }

    public static List<CustomerDTO> saveCustomers(MockMvc mockMvc, List<CustomerDTO> dtos, List<CustomerField> excludes) {

        return dtos.stream()
                .map(c -> {
                    CustomerDTO customer = null;
                    try {
                        customer = saveCustomer(mockMvc, c, excludes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail();
                    }
                    return customer;
                })
                .collect(Collectors.toList());
    }


    public static CustomerDTO saveCustomer(MockMvc mockMvc, CustomerDTO dto, List<CustomerField> excludes) throws Exception {

        AtomicReference<CustomerDTO> result = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
                post(new URI(
                        getCustomerUri(List.of(CUSTOMER_POST_URL), Map.of())))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(customerJsonPath(excludes, "$").value(dto))
                .andDo(mvcResult -> {
                    result.set(
                        objectMapper.readValue(
                            mvcResult.getResponse().getContentAsString(), CustomerDTO.class)
                    );
                });
        return result.get();
    }

    private static void saveInvalidCustomer(MockMvc mockMvc, CustomerDTO dto, List<CustomerField> excludes) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
                post(new URI(
                        getCustomerUri(List.of(CUSTOMER_POST_URL), Map.of())))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    public static CustomerDTO getNoPetsCustomer() {
        final int count = customerCount.incrementAndGet();
        final String name = "Jane No-Pets-" + count;
        final String phoneNumber = "987654" + count;
        final String notes = "Too few pets, only " + count;
        return CustomerDTO.of(
            0, name, phoneNumber, notes, List.of()
        );
    }

    public static String getCustomerUri(List<String> parts, Map<String, Long> idMap) {
        List<Pair<String, Long>> idReplacement;
        if (idMap != null) {
            idReplacement = idMap.entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } else {
            idReplacement = List.of();
        }
        return getUrl(USER_URL, parts, Map.of(), idReplacement);
    }
}