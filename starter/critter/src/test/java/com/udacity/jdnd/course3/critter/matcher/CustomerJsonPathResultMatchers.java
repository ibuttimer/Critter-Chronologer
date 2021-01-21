package com.udacity.jdnd.course3.critter.matcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerJsonPathResultMatchers extends AbstractJsonPathResultMatchers<CustomerField, CustomerDTO> {

    public CustomerJsonPathResultMatchers(List<CustomerField> excludes, String expression, Object... args) {
        super(excludes, CustomerDTO.class, expression, args);
    }

    public static CustomerJsonPathResultMatchers customerJsonPath(List<CustomerField> excludes, String expression, Object... args) {
        return new CustomerJsonPathResultMatchers(excludes, expression, args);
    }

    public static CustomerJsonPathResultMatchers customerJsonPath(String expression, Object... args) {
        return customerJsonPath(List.of(), expression, args);
    }

    @Override
    protected TypeReference<CustomerDTO> getTypeReference() {
        return new DtoTypeReference();
    }

    @Override
    protected TypeReference<List<CustomerDTO>> getListTypeReference() {
        return new ListDtoTypeReference();
    }

    private static class DtoTypeReference extends TypeReference<CustomerDTO> {
        private DtoTypeReference() {
        }
    }

    private static class ListDtoTypeReference extends TypeReference<List<CustomerDTO>> {
        private ListDtoTypeReference() {
        }
    }

    @Override
    protected void assertDto(CustomerDTO expected, CustomerDTO actual) {
        for (CustomerField field : CustomerField.values()) {
            if (!excludes.contains(field)) {
                String errorMsg = field.name() + " does not satisfy criteria";
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId(), errorMsg);
                        break;
                    case NAME:
                        assertEquals(expected.getName(), actual.getName(), errorMsg);
                        break;
                    case PHONE:
                        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber(), errorMsg);
                        break;
                    case NOTES:
                        assertEquals(expected.getNotes(), actual.getNotes(), errorMsg);
                        break;
                    case PETS:
                        assertEquals(expected.getPetIds(), actual.getPetIds(), errorMsg);
                        break;
                }
            }
        }
    }
}
