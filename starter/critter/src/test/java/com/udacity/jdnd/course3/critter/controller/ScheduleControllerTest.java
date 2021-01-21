package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.matcher.CustomerField;
import com.udacity.jdnd.course3.critter.matcher.EmployeeField;
import com.udacity.jdnd.course3.critter.matcher.PetField;
import com.udacity.jdnd.course3.critter.matcher.ScheduleField;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.pet.PetType;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.*;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.controller.CustomerControllerTest.getNoPetsCustomer;
import static com.udacity.jdnd.course3.critter.controller.CustomerControllerTest.saveCustomers;
import static com.udacity.jdnd.course3.critter.controller.EmployeeControllerTest.*;
import static com.udacity.jdnd.course3.critter.controller.PetControllerTest.savePets;
import static com.udacity.jdnd.course3.critter.matcher.ScheduleJsonPathResultMatchers.scheduleJsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScheduleControllerTest extends AbstractContextTest {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    static AtomicInteger scheduleCount = new AtomicInteger();

    @BeforeEach
    void beforeEach() {
        for (AbstractRepository<?> repository :
                List.of(scheduleRepository, petRepository, customerRepository, employeeRepository)) {
            repository.clear();
            assertEquals(0, repository.count());
        }
    }

    @DisplayName("Create new schedule")
    @Test
    public void createSchedule() throws Exception {

        CustomersEmployeesPets cep = getCustomerEmployeesPets(1, 1, 1);

        ScheduleDTO dto = getSchedule(cep.employees, cep.pets, cep.employees.get(0).getSkills());
        saveSchedule(mockMvc, dto, List.of(ScheduleField.ID));    // exclude id
    }

    @DisplayName("Cannot create an invalid schedule")
    @Test
    public void createInvalidSchedule() throws Exception {

        // no employees/pets/activities/date
        ScheduleDTO dto = getSchedule(List.of(), List.of(), Set.of());
        dto.setDate(null);
        saveInvalidSchedule(mockMvc, dto, List.of(ScheduleField.ID));    // exclude id

        CustomersEmployeesPets cep = getCustomerEmployeesPets(1, 1, 1);

        // no pets/activities
        dto = getSchedule(cep.employees, List.of(), Set.of());
        saveInvalidSchedule(mockMvc, dto, List.of(ScheduleField.ID));    // exclude id

        // no activities
        dto = getSchedule(cep.employees, cep.pets, Set.of());
        saveInvalidSchedule(mockMvc, dto, List.of(ScheduleField.ID));    // exclude id

        // invalid date
        dto = getSchedule(cep.employees, cep.pets, cep.employees.get(0).getSkills());
        dto.setDate(null);
        saveInvalidSchedule(mockMvc, dto, List.of(ScheduleField.ID));    // exclude id

        // insufficient skills
        List<EmployeeDTO> employees = saveEmployees(mockMvc,
                List.of(getMondayPetting()),
                List.of(EmployeeField.ID));
        dto = getSchedule(employees, cep.pets, Arrays.stream(EmployeeSkill.values())
                                                    .collect(Collectors.toSet()));
        saveInvalidSchedule(mockMvc, dto, List.of(ScheduleField.ID));    // exclude id
    }

    @DisplayName("Get all schedules")
    @Test
    public void getAllSchedules() throws Exception {

        CustomersEmployeesPets cep = getCustomerEmployeesPets(
                3, 2, (int)Arrays.stream(PetType.values())
                                                    .filter(t -> t != PetType.UNKNOWN)
                                                    .count());

        List<ScheduleDTO> schedules = schedule(cep);

        // get all and verify
        mockMvc.perform(
                    get(getScheduleUri(List.of(), Map.of())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(scheduleJsonPath(List.of(), "$").value(schedules));
    }

    public static class CustomersEmployeesPets {
        List<CustomerDTO> customers;
        List<EmployeeDTO> employees;
        List<PetDTO> pets;

        static CustomersEmployeesPets of(List<CustomerDTO> customers, List<EmployeeDTO> employees, List<PetDTO> pets) {
            CustomersEmployeesPets cep = new CustomersEmployeesPets();
            cep.customers = customers;
            cep.employees = employees;
            cep.pets = pets;
            return cep;
        }
    }

    /**
     * Create specified number of customers, employees and pets, allocating pets to customers
     * @param numCustomers - number of customers to create
     * @param numEmployees - number of employees to create
     * @param numPets - number of pets to create
     * @return
     */
    private CustomersEmployeesPets getCustomerEmployeesPets(int numCustomers, int numEmployees, int numPets) {

        return getCustomerEmployeesPets(ResourceBundle.of()
                        .setMockMvc(mockMvc)
                        .setCustomerRepository(customerRepository)
                        .setEmployeeRepository(employeeRepository)
                        .setPetRepository(petRepository),
                IntStream.range(0, numCustomers)
                        .mapToObj(i -> getNoPetsCustomer())
                        .collect(Collectors.toList()),
                IntStream.range(0, numEmployees)
                        .mapToObj(i -> getEveryDaySkill())
                        .collect(Collectors.toList()), numPets);
    }

    /**
     * Create specified customers and employees and number of pets, allocating pets to customers
     * @param customerList - customers to create
     * @param employeeList - employees to create
     * @param numPets - number of pets to create
     * @return
     */
    public static CustomersEmployeesPets getCustomerEmployeesPets(ResourceBundle bundle,
                      List<CustomerDTO> customerList, List<EmployeeDTO> employeeList, int numPets) {

        long count = bundle.customerRepository.count();
        List<CustomerDTO> customers = saveCustomers(bundle.mockMvc,
                customerList,
                List.of(CustomerField.ID));
        assertEquals(customers.size(), customerList.size());
        assertEquals(count + customers.size(), bundle.customerRepository.count());

        count = bundle.employeeRepository.count();
        List<EmployeeDTO> employees = saveEmployees(bundle.mockMvc,
                employeeList,
                List.of(EmployeeField.ID));
        assertEquals(employees.size(), employeeList.size());
        assertEquals(count + employees.size(), bundle.employeeRepository.count());

        // save pets as customer's
        count = bundle.petRepository.count();
        List<PetType> petTypes = Arrays.stream(PetType.values())
                .filter(t -> t != PetType.UNKNOWN)
                .collect(Collectors.toList());
        List<PetDTO> pets = savePets(bundle.mockMvc,
                IntStream.range(0, numPets)
                        .mapToObj(i -> PetControllerTest.getPet(
                                petTypes.get(i % petTypes.size()), customers.get(i % customers.size()).getId()))
                        .collect(Collectors.toList()),
                List.of(PetField.ID));    // exclude id
        assertEquals(pets.size(), numPets);
        assertEquals(count + pets.size(), bundle.petRepository.count());

        // add the pets to their owners
        for (PetDTO pet : pets) {
            customers.stream()
                    .filter(c -> c.getId() == pet.getOwnerId())
                    .findFirst()
                    .ifPresent(c -> c.addPetId(pet.getId()));
        }
        return CustomersEmployeesPets.of(customers, employees, pets);
    }

    /**
     * Create schedules, one for each customer
     * @param cep - customer, employee and pet details
     * @return
     */
    private List<ScheduleDTO> schedule(CustomersEmployeesPets cep) {
        EmployeeSkill[] skills = EmployeeSkill.values();
        AtomicInteger index = new AtomicInteger();

        return saveSchedules(mockMvc,
                cep.customers.stream()
                        .map(c -> getSchedule(
                                cep.employees.subList(0, 1 + (index.get() % cep.employees.size())), // 1 to all employees
                                cep.pets.stream()
                                        .filter(p -> c.getPetIds().contains(p.getId()))
                                        .collect(Collectors.toList()),  // pets corresponding to the customer
                                Arrays.stream(skills)
                                        .limit(1 + (index.getAndIncrement() % skills.length))
                                        .collect(Collectors.toSet())))  // increasing number of skills
                        .collect(Collectors.toList()),
                List.of(ScheduleField.ID));
    }

    @DisplayName("Get schedule by pet")
    @Test
    public void getScheduleByPet() throws Exception {

        // save customers/employees/pets
        CustomersEmployeesPets cep = getCustomerEmployeesPets(
                3, 2, (int)Arrays.stream(PetType.values())
                        .filter(t -> t != PetType.UNKNOWN)
                        .count());

        // create schedules
        List<ScheduleDTO> schedules = schedule(cep);

        // select pet
        CustomerDTO customer = cep.customers.get(cep.customers.size() / 2);
        List<Long> pets = customer.getPetIds();
        long petId = pets.get(pets.size() / 2);
        Optional<PetDTO> petOpt = cep.pets.stream()
                .filter(p -> p.getId() == petId)
                .findFirst();

        if (petOpt.isEmpty()) {
            fail(String.format("Pet with id %d not identified", petId));
        }

        // find pets schedule
        petOpt.ifPresent(pet -> {
            Optional<ScheduleDTO> scheduleOpt = schedules.stream()
                    .filter(s -> s.getPetIds().contains(pet.getId()))
                    .findFirst();

            if (scheduleOpt.isEmpty()) {
                fail(String.format("Schedule not identified for pet with id %d", pet.getId()));
            }

            scheduleOpt.ifPresent(schedule -> {
                // get schedule for pet and verify
                try {
                    mockMvc.perform(
                                get(getScheduleUri(List.of(GET_SCHEDULE_BY_PET_URL), Map.of(PET_ID_PATTERN, pet.getId()))))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$").isArray())
                            .andExpect(scheduleJsonPath(List.of(), "$").value(
                                    List.of(schedule)));
                } catch (Exception e) {
                    e.printStackTrace();
                    fail();
                }
            });
        });
    }

    @DisplayName("Get schedule by id")
    @Test
    public void getScheduleById() throws Exception {

        // save customers/employees/pets
        CustomersEmployeesPets cep = getCustomerEmployeesPets(
                3, 2, (int)Arrays.stream(PetType.values())
                        .filter(t -> t != PetType.UNKNOWN)
                        .count());

        // create schedules
        List<ScheduleDTO> schedules = schedule(cep);
        ScheduleDTO schedule = schedules.get(0);

        mockMvc.perform(
                    get(getScheduleUri(List.of(GET_SCHEDULE_BY_ID_URL), Map.of(SCHEDULE_ID_PATTERN, schedule.getId()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(scheduleJsonPath(List.of(), "$").value(schedule));
    }

    @DisplayName("Get non-existent schedule")
    @Test
    public void getInvalidScheduleById() throws Exception {

        mockMvc.perform(
                    get(getScheduleUri(List.of(GET_SCHEDULE_BY_ID_URL), Map.of(SCHEDULE_ID_PATTERN, 1000L))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Get schedule by employee")
    @Test
    public void getScheduleByEmployee() throws Exception {

        // save customers/employees/pets
        CustomersEmployeesPets cep = getCustomerEmployeesPets(
                3, 2, (int)Arrays.stream(PetType.values())
                        .filter(t -> t != PetType.UNKNOWN)
                        .count());

        // create schedules
        List<ScheduleDTO> schedules = schedule(cep);

        // select employee
        EmployeeDTO employee = cep.employees.get(cep.employees.size() / 2);

        // find employees schedule
        Optional<ScheduleDTO> scheduleOpt = schedules.stream()
                .filter(s -> s.getEmployeeIds().contains(employee.getId()))
                .findFirst();

        if (scheduleOpt.isEmpty()) {
            fail(String.format("Schedule not identified for employee with id %d", employee.getId()));
        }

        scheduleOpt.ifPresent(schedule -> {
            // get schedule for employee and verify
            try {
                mockMvc.perform(
                            get(getScheduleUri(List.of(GET_SCHEDULE_BY_EMPLOYEE_URL), Map.of(EMPLOYEE_ID_PATTERN, employee.getId()))))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(scheduleJsonPath(List.of(), "$").value(
                                List.of(schedule)));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });
    }

    @DisplayName("Get schedule by customer/owner")
    @Test
    public void getScheduleByCustomer() throws Exception {

        // save customers/employees/pets
        CustomersEmployeesPets cep = getCustomerEmployeesPets(
                3, 2, (int)Arrays.stream(PetType.values())
                        .filter(t -> t != PetType.UNKNOWN)
                        .count());

        // create schedules
        List<ScheduleDTO> schedules = schedule(cep);

        // select customer
        CustomerDTO customer = cep.customers.get(cep.customers.size() / 2);

        // find customers schedule
        Optional<ScheduleDTO> scheduleOpt = schedules.stream()
                .filter(s -> {
                    // find schedule with one of customer's pets
                    Optional<Long> pId = customer.getPetIds().stream()
                            .filter(p -> s.getPetIds().contains(p))
                            .findFirst();
                    return pId.isPresent();
                })
                .findFirst();

        if (scheduleOpt.isEmpty()) {
            fail(String.format("Schedule not identified for customer with id %d", customer.getId()));
        }

        scheduleOpt.ifPresent(schedule -> {
            // get schedule for customer and verify
            try {
                mockMvc.perform(
                            get(getScheduleUri(List.of(GET_SCHEDULE_BY_CUSTOMER_URL), Map.of(CUSTOMER_ID_PATTERN, customer.getId()))))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(scheduleJsonPath(List.of(), "$").value(
                                List.of(schedule)));

                mockMvc.perform(
                            get(getScheduleUri(List.of(GET_SCHEDULE_BY_OWNER_URL), Map.of(OWNER_ID_PATTERN, customer.getId()))))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(scheduleJsonPath(List.of(), "$").value(
                                List.of(schedule)));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });
    }

    public static List<ScheduleDTO> saveSchedules(MockMvc mockMvc, List<ScheduleDTO> dtos, List<ScheduleField> excludes) {

        return dtos.stream()
                .map(c -> {
                    ScheduleDTO schedule = null;
                    try {
                        schedule = saveSchedule(mockMvc, c, excludes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail();
                    }
                    return schedule;
                })
                .collect(Collectors.toList());
    }


    public static ScheduleDTO saveSchedule(MockMvc mockMvc, ScheduleDTO dto, List<ScheduleField> excludes) throws Exception {

        AtomicReference<ScheduleDTO> result = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mockMvc.perform(
                post(new URI(
                            getScheduleUri(List.of(), Map.of())))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(scheduleJsonPath(excludes, "$").value(dto))
                .andDo(mvcResult -> {
                    result.set(
                            objectMapper.readValue(
                                    mvcResult.getResponse().getContentAsString(), ScheduleDTO.class)
                    );
                });
        return result.get();
    }

    private static void saveInvalidSchedule(MockMvc mockMvc, ScheduleDTO dto, List<ScheduleField> excludes) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mockMvc.perform(
                post(new URI(
                            getScheduleUri(List.of(), Map.of())))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public static ScheduleDTO getSchedule(List<EmployeeDTO> employees, List<PetDTO> pets, Set<EmployeeSkill> activities) {
        final int count = scheduleCount.incrementAndGet();
        return ScheduleDTO.of(0,
                employees.stream()
                        .map(EmployeeDTO::getId)
                        .collect(Collectors.toList()),
                pets.stream()
                        .map(PetDTO::getId)
                        .collect(Collectors.toList()),
                LocalDate.now().plusDays(count),
                activities);
    }


    public static String getScheduleUri(List<String> parts, Map<String, Long> idMap) {
        List<Pair<String, Long>> idReplacement;
        if (idMap != null) {
            idReplacement = idMap.entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } else {
            idReplacement = List.of();
        }
        return getUrl(SCHEDULE_URL, parts, Map.of(), idReplacement);
    }

}