package com.udacity.jdnd.course3.critter.matcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udacity.jdnd.course3.critter.pet.PetDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetJsonPathResultMatchers extends AbstractJsonPathResultMatchers<PetField, PetDTO> {

    public PetJsonPathResultMatchers(List<PetField> excludes, String expression, Object... args) {
        super(excludes, PetDTO.class, expression, args);
    }

    public static PetJsonPathResultMatchers petJsonPath(List<PetField> excludes, String expression, Object... args) {
        return new PetJsonPathResultMatchers(excludes, expression, args);
    }

    public static PetJsonPathResultMatchers petJsonPath(String expression, Object... args) {
        return petJsonPath(List.of(), expression, args);
    }

    @Override
    protected TypeReference<PetDTO> getTypeReference() {
        return new DtoTypeReference();
    }

    @Override
    protected TypeReference<List<PetDTO>> getListTypeReference() {
        return new ListDtoTypeReference();
    }

    private static class DtoTypeReference extends TypeReference<PetDTO> {
        private DtoTypeReference() {
        }
    }

    private static class ListDtoTypeReference extends TypeReference<List<PetDTO>> {
        private ListDtoTypeReference() {
        }
    }

    @Override
    protected ObjectMapper registerMapperModules(ObjectMapper mapper) {
        return mapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void assertDto(PetDTO expected, PetDTO actual) {
        for (PetField field : PetField.values()) {
            if (!excludes.contains(field)) {
                String errorMsg = field.name() + " does not satisfy criteria";
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId(), errorMsg);
                        break;
                    case NAME:
                        assertEquals(expected.getName(), actual.getName(), errorMsg);
                        break;
                    case TYPE:
                        assertEquals(expected.getType(), actual.getType(), errorMsg);
                        break;
                    case OWNER:
                        assertEquals(expected.getOwnerId(), actual.getOwnerId(), errorMsg);
                        break;
                    case BIRTH_DATE:
                        assertEquals(expected.getBirthDate(), actual.getBirthDate(), errorMsg);
                        break;
                }
            }
        }
    }
}
