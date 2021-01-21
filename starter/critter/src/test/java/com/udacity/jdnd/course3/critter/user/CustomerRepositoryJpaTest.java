package com.udacity.jdnd.course3.critter.user;

import com.google.common.collect.Maps;
import com.udacity.jdnd.course3.critter.matcher.CustomerField;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@Import(CustomerRepository.class)
public
class CustomerRepositoryJpaTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    CustomerRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.clear();
        assertEquals(0, repository.findAll().size());
    }

    @DisplayName("Save customer")
    @Test
    void saveCustomer() {
        saveCustomer(
                getMultiplePetsCustomer());
    }

    private Customer saveCustomer(Customer toSave) {
        return saveCustomer(toSave, testEntityManager, repository);
    }

    private Customer getByName(String name) {
        return getByName(name, repository);
    }

    public static Customer saveCustomer(Customer toSave, TestEntityManager testEntityManager, CustomerRepository repository) {
        testEntityManager.persist(toSave);

        Customer created = getByName(toSave.getName(), repository);
        assertCustomer(toSave, created, CustomerField.ID);  // exclude id

        return created;
    }

    public static Customer getByName(String name, CustomerRepository repository) {
        return assertOnlyOne(
                repository.findByName(name), null);
    }

    static Customer assertOnlyOne(List<Customer> list, Customer expected) {
        assertEquals(1, list.size());
        Customer customer = list.get(0);
        if (expected != null) {
            assertCustomer(expected, customer);
        }
        return customer;
    }

    @DisplayName("Find customer by name/id/phone")
    @Test
    void findCustomer() {
        final String tooMany = "tooMany";
        final String tooFew = "tooFew";
        Map<String, Customer> customers = Maps.newHashMap();
        Customer customer;

        // save employees
        for (Customer toSave : List.of(getMultiplePetsCustomer(), getNoPetsCustomer())) {
            Customer created = saveCustomer(toSave);

            customer = repository.findById(created.getId());
            assertCustomer(created, customer);

            customer = assertOnlyOne(
                    repository.findByName(created.getName()), created);

            customer = assertOnlyOne(
                    repository.findByPhoneNumber(created.getPhoneNumber()), created);

            customers.put(customer.getPets().size() == 1 ? tooFew : tooMany, customer);
        }
    }

    @DisplayName("Update customer name/phone/notes/pets")
    @Test
    void updateCustomer() {
        Customer created = saveCustomer(getNoPetsCustomer());

        final String newName = "New Name";
        created.setName(newName);

        // update name
        Customer updated = getByName(newName);
        assertEquals(created, updated);

        // update phone
        final String newPhone = "+123456789";
        created.setPhoneNumber(newPhone);
        updated = assertOnlyOne(
                repository.findByPhoneNumber(newPhone), created);

        // update note
        final String newNote = "This is a new note";
        created.setNotes(newNote);
        updated = repository.findById(created.getId());
        assertEquals(created, updated);

        // update pets
        assertEquals(0, created.getPets().size());
        List<Pet> pets = List.of(
                Pet.of(0, "Johnny ComeLately", PetType.OTHER, created, LocalDate.now(), "Pet #1")
        );
        created.setPets(pets);
        assertEquals(pets.size(), created.getPets().size());
        updated = repository.findById(created.getId());
        assertEquals(pets.size(), updated.getPets().size());
        assertEquals(created, updated);
    }

    @DisplayName("Delete customer")
    @Test
    void deleteCustomer() {
        /* use customer with no pets for this test to avoid 'Referential integrity constraint violation' as the pets
            hold a foreign key reference to the owner */
        Customer created = saveCustomer(getNoPetsCustomer());

        int affected = repository.delete(created.getId());
        assertEquals(1, affected);
        assertEquals(0, repository.findAll().size());
    }

    public static <C extends ICustomer> void assertCustomer(C expected, C actual, CustomerField...exclude) {
        for (CustomerField field : CustomerField.values()) {
            if (!Arrays.asList(exclude).contains(field)) {
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId());
                        break;
                    case NAME:
                        assertEquals(expected.getName(), actual.getName());
                        break;
                    case PHONE:
                        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
                        break;
                    case NOTES:
                        assertEquals(expected.getNotes(), actual.getNotes());
                        break;
                    case PETS:
                        if (areInstance(expected, actual, ICustomer.ICustomerEntity.class)) {
                            assertEquals(((ICustomer.ICustomerEntity)expected).getPets(),
                                    ((ICustomer.ICustomerEntity)actual).getPets());
                        } else if (areInstance(expected, actual, ICustomer.ICustomerDTO.class)) {
                            assertEquals(((ICustomer.ICustomerDTO)expected).getPetIds(),
                                    ((ICustomer.ICustomerDTO)actual).getPetIds());
                        } else {
                            fail("Pets assertion not implemented for " + expected.getClass().getSimpleName()
                                        + " " + actual.getClass().getSimpleName());
                        }
                        break;
                }
            }
        }
    }

    static boolean areInstance(Object a, Object b, Class<?> cls) {
        return cls.isInstance(a) && cls.isInstance(b);
    }

    public static Customer getMultiplePetsCustomer() {
        final String name = "Fred Many-Pets";
        final String phoneNumber = "123456";
        final String notes = "Too many pets";

        return Customer.of(0, name, phoneNumber, notes,
                Arrays.stream(PetType.values())
                    .filter(t -> t != PetType.UNKNOWN)
                    .map(t -> Pet.of(0, t.name() + "'s name", t, null, LocalDate.now(), t.name() + "'s note"))
                    .collect(Collectors.toList()));
    }

    public static Customer getNoPetsCustomer() {
        final String name = "Jane No-Pets";
        final String phoneNumber = "987654";
        final String notes = "Too few pets";
        return Customer.of(0, name, phoneNumber, notes, List.of());
    }

}