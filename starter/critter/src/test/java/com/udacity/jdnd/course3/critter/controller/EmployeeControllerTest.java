package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.controller.ScheduleControllerTest.*;
import com.udacity.jdnd.course3.critter.matcher.EmployeeField;
import com.udacity.jdnd.course3.critter.matcher.ScheduleField;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.controller.CustomerControllerTest.getNoPetsCustomer;
import static com.udacity.jdnd.course3.critter.controller.ScheduleControllerTest.*;
import static com.udacity.jdnd.course3.critter.matcher.EmployeeJsonPathResultMatchers.employeeJsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest extends AbstractContextTest {

    private static final Logger log = LoggerFactory.getLogger(EmployeeControllerTest.class);

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    private ResourceBundle bundle;

    static AtomicInteger employeeCount = new AtomicInteger();

    @BeforeEach
    void beforeEach() {
        if (bundle == null) {
            bundle = ResourceBundle.of()
                    .setEmployeeRepository(employeeRepository)
                    .setCustomerRepository(customerRepository)
                    .setPetRepository(petRepository)
                    .setScheduleRepository(scheduleRepository)
                    .setMockMvc(mockMvc);
        }
        for (AbstractRepository<?> repository :
                List.of(scheduleRepository, petRepository, customerRepository, employeeRepository)) {
            repository.clear();
            assertEquals(0, repository.count());
        }
    }

    @DisplayName("Create new employee")
    @Test
    public void createEmployee() throws Exception {
        saveEmployee(mockMvc, getEveryDaySkill(), List.of(EmployeeField.ID));
    }

    @DisplayName("Cannot create invalid employee")
    @Test
    public void createInvalidEmployee() throws Exception {
        EmployeeDTO employee = getEveryDaySkill();
        employee.setName("");
        saveInvalidEmployee(mockMvc, employee, List.of(EmployeeField.ID));
    }

    @DisplayName("Get employee by id")
    @Test
    public void getEmployeeById() throws Exception {

        EmployeeDTO employeeDTO = saveEmployee(mockMvc, getMondayPetting(), List.of(EmployeeField.ID));    // exclude id

        mockMvc.perform(
                get(new URI(
                    getEmployeeUri(List.of(EMPLOYEE_GET_BY_ID_URL), Map.of(EMPLOYEE_ID_PATTERN, employeeDTO.getId())))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(employeeJsonPath(List.of(), "$").value(employeeDTO));
    }

    @DisplayName("Get invalid employee")
    @Test
    public void getInvalidEmployeeById() throws Exception {

        mockMvc.perform(
                get(new URI(
                    getEmployeeUri(List.of(EMPLOYEE_GET_BY_ID_URL), Map.of(EMPLOYEE_ID_PATTERN, 1000L)))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update employee availability")
    @Test
    public void updateEmployeeAvailability() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        EmployeeDTO employeeDTO = saveEmployee(mockMvc, getMondayPetting(), List.of(EmployeeField.ID));    // exclude id

        Set<DayOfWeek> newDays = Sets.newHashSet(DayOfWeek.values());
        employeeDTO.setDaysAvailable(newDays);

        mockMvc.perform(
                put(new URI(
                    getEmployeeUri(List.of(EMPLOYEE_PUT_BY_ID_URL), Map.of(EMPLOYEE_ID_PATTERN, employeeDTO.getId()))))
                        .content(objectMapper.writeValueAsString(
                                newDays))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(employeeJsonPath(List.of(), "$").value(employeeDTO));
    }

    @DisplayName("Find employees for service")
    @Test
    public void findEmployeesForService() throws Exception {

        // create employees/customer/pets
        final String SAM = "Sam Feeder";
        final String JANE = "Jane Feeder-Walker";
        final String FRED = "Fred Feeder-Walker";
        final String JIM = "Dr. Jim";
        CustomersEmployeesPets cep = getCustomerEmployeesPets(bundle,
                                        List.of(getNoPetsCustomer()),   // 1 customer
                                        List.of(                        // 4 employees
                                            EmployeeDTO.of(
                                            0, SAM,
                                                Set.of(EmployeeSkill.FEEDING),
                                                Set.of(DayOfWeek.values())),
                                            EmployeeDTO.of(
                                            0, JANE,
                                                Set.of(
                                                    EmployeeSkill.FEEDING, EmployeeSkill.WALKING),
                                                Arrays.stream(DayOfWeek.values())
                                                    .filter(d -> d != DayOfWeek.TUESDAY)    // doesn't work tuesday
                                                    .collect(Collectors.toSet())),
                                            EmployeeDTO.of(
                                            0, FRED,
                                                Set.of(
                                                    EmployeeSkill.FEEDING, EmployeeSkill.WALKING),
                                                    Set.of(DayOfWeek.values())),
                                            EmployeeDTO.of(
                                            0, JIM,
                                                Set.of(
                                                    EmployeeSkill.MEDICATING, EmployeeSkill.SHAVING),
                                                Set.of(DayOfWeek.values()))
                                        ),
                                5);     // 5 pets

        /* Generate the following schedules
            Date        DoW     Sam                 Jane                        Fred                        Jim
                                Work    Feeding     Work    Feeding Walking     Work    Feeding Walking     Work    Medicating  Shaving
            19/01/2021  Tue     Y       B           N       -       -           Y       B       -           Y       B           -
            20/01/2021  Wed     Y       B           Y       -       -           Y       B       -           Y       -           B
            21/01/2021  Thu     Y       -           Y       -       -           Y       -       -           Y       -           -
         */
        final EmployeeDTO sam = findOrFail(cep.employees, SAM);
        final EmployeeDTO jane = findOrFail(cep.employees, JANE);
        final EmployeeDTO fred = findOrFail(cep.employees, FRED);
        final EmployeeDTO jim = findOrFail(cep.employees, JIM);
        final LocalDate tue19th = LocalDate.of(2021, 1, 19);
        final LocalDate wed20th = LocalDate.of(2021, 1, 20);
        final LocalDate thu21th = LocalDate.of(2021, 1, 21);
        ScheduleDTO tueSchedule = getSchedule(
                List.of(sam, fred, jim),
                cep.pets.subList(0, cep.pets.size() / 2),
                Set.of(EmployeeSkill.FEEDING, EmployeeSkill.MEDICATING)
        );
        tueSchedule.setDate(tue19th);
        ScheduleDTO wedSchedule = getSchedule(
                List.of(sam, fred, jim),
                cep.pets.subList(cep.pets.size() / 2, cep.pets.size()),
                Set.of(EmployeeSkill.FEEDING, EmployeeSkill.SHAVING)
        );
        wedSchedule.setDate(wed20th);

        List<ScheduleDTO> schedules = saveSchedules(mockMvc,
            List.of(tueSchedule, wedSchedule),
            List.of(ScheduleField.ID));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        for (SkillsDateEmployees sde : List.of(
            // check nobody available for feeding on tue
            SkillsDateEmployees.of(Set.of(EmployeeSkill.FEEDING), tue19th, List.of()),
            // check jane available for feeding on wed
            SkillsDateEmployees.of(Set.of(EmployeeSkill.FEEDING), wed20th, List.of(jane)),
            // check jane available for walking & feeding on wed
            SkillsDateEmployees.of(Set.of(EmployeeSkill.WALKING, EmployeeSkill.FEEDING), wed20th, List.of(jane)),
            // check jane & fred available for walking & feeding on thu
            SkillsDateEmployees.of(Set.of(EmployeeSkill.WALKING, EmployeeSkill.FEEDING), thu21th, List.of(jane, fred)),
            // check sam, jane & fred available for feeding on thu
            SkillsDateEmployees.of(Set.of(EmployeeSkill.FEEDING), thu21th, List.of(sam, jane, fred))
        )) {
            log.info(String.format("Testing [%s] on %s, expecting [%s]",
                    Joiner.on(',').join(sde.skills), sde.date, Joiner.on(',').join(sde.employees)));

            mockMvc.perform(
                    get(new URI(
                            getEmployeeUri(List.of(EMPLOYEE_AVAILABILITY_URL), null)))
                            .content(objectMapper.writeValueAsString(
                                    EmployeeRequestDTO.of(sde.skills, sde.date)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(employeeJsonPath(List.of(), "$").value(sde.employees));
        }
    }

    private static class SkillsDateEmployees {
        Set<EmployeeSkill> skills;
        LocalDate date;
        List<EmployeeDTO> employees;

        public static SkillsDateEmployees of(Set<EmployeeSkill> skills, LocalDate date, List<EmployeeDTO> employees) {
            SkillsDateEmployees sde = new SkillsDateEmployees();
            sde.skills = skills;
            sde.date = date;
            sde.employees = employees;
            return sde;
        }
    }


    private EmployeeDTO findOrFail(List<EmployeeDTO> list, String name) {
        AtomicReference<EmployeeDTO> reference = new AtomicReference<>();
        list.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst().ifPresentOrElse(reference::set, ()-> {
            fail(name + " not found");
        });
        return reference.get();
    }

    public static List<EmployeeDTO> saveEmployees(MockMvc mockMvc, List<EmployeeDTO> dtos, List<EmployeeField> excludes) {

        return dtos.stream()
                .map(c -> {
                    EmployeeDTO dto = null;
                    try {
                        dto = saveEmployee(mockMvc, c, excludes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail();
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static EmployeeDTO saveEmployee(MockMvc mockMvc, EmployeeDTO dto, List<EmployeeField> excludes) throws Exception {

        AtomicReference<EmployeeDTO> result = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
                post(new URI(
                    getEmployeeUri(List.of(EMPLOYEE_POST_URL), null)))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(employeeJsonPath(excludes, "$").value(dto))
                .andDo(mvcResult -> {
                    result.set(
                            objectMapper.readValue(
                                    mvcResult.getResponse().getContentAsString(), EmployeeDTO.class)
                    );
                });
        return result.get();
    }

    private static void saveInvalidEmployee(MockMvc mockMvc, EmployeeDTO dto, List<EmployeeField> excludes) throws Exception {

        AtomicReference<EmployeeDTO> result = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
                post(new URI(
                    getEmployeeUri(List.of(EMPLOYEE_POST_URL), null)))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public static EmployeeDTO getEveryDaySkill() {
        final String name = "Every Day-Skill-" + employeeCount.incrementAndGet();
        return EmployeeDTO.of(0, name, Set.of(EmployeeSkill.values()), Set.of(DayOfWeek.values()));
    }

    public static EmployeeDTO getMondayPetting() {
        final String name = "Monday Petting-" + employeeCount.incrementAndGet();
        return EmployeeDTO.of(0, name, Set.of(EmployeeSkill.PETTING), Set.of(DayOfWeek.MONDAY));
    }

    public static String getEmployeeUri(List<String> parts, Map<String, Long> idMap) {
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