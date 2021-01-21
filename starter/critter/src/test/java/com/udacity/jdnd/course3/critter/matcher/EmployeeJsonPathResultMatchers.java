package com.udacity.jdnd.course3.critter.matcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeJsonPathResultMatchers extends AbstractJsonPathResultMatchers<EmployeeField, EmployeeDTO> {

    public EmployeeJsonPathResultMatchers(List<EmployeeField> excludes, String expression, Object... args) {
        super(excludes, EmployeeDTO.class, expression, args);
    }

    public static EmployeeJsonPathResultMatchers employeeJsonPath(List<EmployeeField> excludes, String expression, Object... args) {
        return new EmployeeJsonPathResultMatchers(excludes, expression, args);
    }

    public static EmployeeJsonPathResultMatchers employeeJsonPath(String expression, Object... args) {
        return employeeJsonPath(List.of(), expression, args);
    }

    @Override
    protected TypeReference<EmployeeDTO> getTypeReference() {
        return new DtoTypeReference();
    }

    @Override
    protected TypeReference<List<EmployeeDTO>> getListTypeReference() {
        return new ListDtoTypeReference();
    }

    private static class DtoTypeReference extends TypeReference<EmployeeDTO> {
        private DtoTypeReference() {
        }
    }

    private static class ListDtoTypeReference extends TypeReference<List<EmployeeDTO>> {
        private ListDtoTypeReference() {
        }
    }

    @Override
    protected void assertDto(EmployeeDTO expected, EmployeeDTO actual) {
        for (EmployeeField field : EmployeeField.values()) {
            if (!excludes.contains(field)) {
                String errorMsg = field.name() + " does not satisfy criteria";
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId(), errorMsg);
                        break;
                    case NAME:
                        assertEquals(expected.getName(), actual.getName(), errorMsg);
                        break;
                    case SKILLS:
                        assertEquals(expected.getSkills(), actual.getSkills(), errorMsg);
                        break;
                    case DAYS_AVAILABLE:
                        assertEquals(expected.getDaysAvailable(), actual.getDaysAvailable(), errorMsg);
                        break;
                }
            }
        }
    }
}
