package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udacity.jdnd.course3.critter.matcher.CustomerField;
import com.udacity.jdnd.course3.critter.matcher.PetField;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.pet.PetType;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.controller.CustomerControllerTest.getNoPetsCustomer;
import static com.udacity.jdnd.course3.critter.controller.CustomerControllerTest.saveCustomer;
import static com.udacity.jdnd.course3.critter.matcher.PetJsonPathResultMatchers.petJsonPath;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PetControllerTest extends AbstractContextTest {

    @Autowired
    PetRepository repository;

    static AtomicInteger petCount = new AtomicInteger();

    @BeforeEach
    void beforeEach() {
        repository.clear();
        assertEquals(0, repository.findAll().size());
    }

    @DisplayName("Create new pet")
    @Test
    public void createPet() throws Exception {

        PetDTO dto = getPet(PetType.DOG);
        savePet(mockMvc, dto, List.of(PetField.ID, PetField.OWNER));    // exclude id & owner
    }

    @DisplayName("Cannot create invalid pet")
    @Test
    public void createInvalidPet() throws Exception {

        PetDTO dto = getPet(PetType.DOG);
        dto.setName("");
        saveInvalidPet(mockMvc, dto, List.of(PetField.ID, PetField.OWNER));    // exclude id & owner
    }

    @DisplayName("Get pet by id")
    @Test
    public void getPet() throws Exception {

        List<PetField> excludes = List.of(PetField.ID, PetField.OWNER);

        // save a pet of every type
        List<PetDTO> pets = savePets(mockMvc,
                Arrays.stream(PetType.values())
                    .filter(t -> t != PetType.UNKNOWN)
                    .map(PetControllerTest::getPet)
                    .collect(Collectors.toList()),
                excludes);
        PetDTO pet = pets.get(pets.size() / 2);

        // get and verify middle pet
        mockMvc.perform(
                    get(getPetUri(List.of(PET_ID_URL), Map.of(PET_ID_PATTERN, pet.getId()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(petJsonPath(excludes, "$").value(pet));
    }

    @DisplayName("Get non-existent pet")
    @Test
    public void getNonExistentPet() throws Exception {

        mockMvc.perform(
                    get(getPetUri(List.of(PET_ID_URL), Map.of(PET_ID_PATTERN, 1000L))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Get all pets")
    @Test
    public void getAllPets() throws Exception {

        List<PetField> excludes = List.of(PetField.ID, PetField.OWNER);

        // save a pet of every type
        List<PetDTO> pets = savePets(mockMvc,
                Arrays.stream(PetType.values())
                    .filter(t -> t != PetType.UNKNOWN)
                    .map(PetControllerTest::getPet)
                    .collect(Collectors.toList()),
                excludes);

        // get all and verify
        mockMvc.perform(
                    get(getPetUri(List.of(), Map.of())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(petJsonPath(excludes, "$").value(pets));
    }

    @DisplayName("Get pets by owner")
    @Test
    public void getPetsByOwner() throws Exception {

        CustomerDTO customer = saveCustomer(mockMvc, getNoPetsCustomer(), List.of(CustomerField.ID));    // exclude id

        List<PetField> excludes = List.of(PetField.ID, PetField.OWNER);

        // save a pet of each type, setting the owner for every second one
        AtomicInteger count = new AtomicInteger();
        List<PetDTO> allPets = savePets(mockMvc,
                Arrays.stream(PetType.values())
                    .filter(t -> t != PetType.UNKNOWN)
                    .map(PetControllerTest::getPet)
                    .peek(p -> {
                        if (count.getAndIncrement() % 2 == 0) {
                            p.setOwnerId(customer.getId());
                        }
                    })
                    .collect(Collectors.toList()),
                excludes);

        List<PetDTO> pets = allPets.stream()
                .filter(p -> p.getOwnerId() != 0)
                .collect(Collectors.toList());

        assertTrue(pets.size() < allPets.size());

        // get pets for owner and verify
        mockMvc.perform(
                    get(getPetUri(List.of(PET_GET_BY_OWNER_URL), Map.of(OWNER_ID_PATTERN, customer.getId()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(petJsonPath(excludes, "$").value(pets));
    }

    @DisplayName("Update pet")
    @Test
    public void updatePet() throws Exception {

        ObjectMapper objectMapper = getPetObjectMapper();

        // save customer
        CustomerDTO customer = saveCustomer(mockMvc, getNoPetsCustomer(), List.of(CustomerField.ID));    // exclude id
        CustomerDTO buyer = saveCustomer(mockMvc, getNoPetsCustomer(), List.of(CustomerField.ID));    // exclude id

        // save pet as customer's
        PetDTO pet = savePet(mockMvc, PetDTO.of(
                0, PetType.DOG, "Fido", customer.getId(), LocalDate.now(), "Bad dog"),
                List.of(PetField.ID));    // exclude id

        // update and verify
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0: pet.setName("Fifi");                                    break;
                case 1: pet.setType(PetType.CAT);                               break;
                case 2: pet.setBirthDate(pet.getBirthDate().minusYears(1L));    break;
                case 3: pet.setNotes("very confused");                          break;
                case 4: pet.setOwnerId(buyer.getId());                          break;
                default:    fail("Update test, missing one");                   break;
            }
            mockMvc.perform(
                    put(new URI(
                            getPetUri(List.of(PET_ID_URL), Map.of(PET_ID_PATTERN, pet.getId()))))
                            .content(objectMapper.writeValueAsString(pet))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(petJsonPath(List.of(), "$").value(pet));
        }
    }


    public static List<PetDTO> savePets(MockMvc mockMvc, List<PetDTO> dtos, List<PetField> excludes) {

        return dtos.stream()
                .map(c -> {
                    PetDTO pet = null;
                    try {
                        pet = savePet(mockMvc, c, excludes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail();
                    }
                    return pet;
                })
                .collect(Collectors.toList());
    }
    
    
    public static PetDTO savePet(MockMvc mockMvc, PetDTO dto, List<PetField> excludes) throws Exception {
        return persistPet(mockMvc, dto, List.of(), excludes);
    }

    public static PetDTO updatePet(MockMvc mockMvc, PetDTO dto, List<PetField> excludes) throws Exception {
        return persistPet(mockMvc, dto, List.of(PET_ID_URL), excludes);
    }

    public static ObjectMapper getPetObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    public static PetDTO persistPet(MockMvc mockMvc, PetDTO dto, List<String> parts, List<PetField> excludes) throws Exception {

        AtomicReference<PetDTO> result = new AtomicReference<>();
        ObjectMapper objectMapper = getPetObjectMapper();

        mockMvc.perform(
                post(new URI(
                            getPetUri(parts, Map.of())))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(petJsonPath(excludes, "$").value(dto))
                .andDo(mvcResult -> {
                    result.set(
                            objectMapper.readValue(
                                    mvcResult.getResponse().getContentAsString(), PetDTO.class)
                    );
                });
        return result.get();
    }

    private static void saveInvalidPet(MockMvc mockMvc, PetDTO dto, List<PetField> excludes) throws Exception {

        ObjectMapper objectMapper = getPetObjectMapper();

        mockMvc.perform(
                post(new URI(
                            getPetUri(List.of(), Map.of())))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public static PetDTO getPet(PetType type) {
        return getPet(type, 0);
    }

    public static PetDTO getPet(PetType type, long ownerId) {
        final int count = petCount.incrementAndGet();
        final String name = type.name() + " pet " + count;
        final String notes = "Note for " + type.name() + " " + count;
        return PetDTO.of(0, type, name, ownerId, LocalDate.now().plusDays(count), notes);
    }


    public static String getPetUri(List<String> parts, Map<String, Long> idMap) {
        List<Pair<String, Long>> idReplacement;
        if (idMap != null) {
            idReplacement = idMap.entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } else {
            idReplacement = List.of();
        }
        return getUrl(PET_URL, parts, Map.of(), idReplacement);
    }

}