package com.udacity.jdnd.course3.critter.matcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleJsonPathResultMatchers extends AbstractJsonPathResultMatchers<ScheduleField, ScheduleDTO> {

    public ScheduleJsonPathResultMatchers(List<ScheduleField> excludes, String expression, Object... args) {
        super(excludes, ScheduleDTO.class, expression, args);
    }

    public static ScheduleJsonPathResultMatchers scheduleJsonPath(List<ScheduleField> excludes, String expression, Object... args) {
        return new ScheduleJsonPathResultMatchers(excludes, expression, args);
    }

    public static ScheduleJsonPathResultMatchers scheduleJsonPath(String expression, Object... args) {
        return scheduleJsonPath(List.of(), expression, args);
    }

    @Override
    protected TypeReference<ScheduleDTO> getTypeReference() {
        return new DtoTypeReference();
    }

    @Override
    protected TypeReference<List<ScheduleDTO>> getListTypeReference() {
        return new ListDtoTypeReference();
    }

    private static class DtoTypeReference extends TypeReference<ScheduleDTO> {
        private DtoTypeReference() {
        }
    }

    private static class ListDtoTypeReference extends TypeReference<List<ScheduleDTO>> {
        private ListDtoTypeReference() {
        }
    }

    @Override
    protected ObjectMapper registerMapperModules(ObjectMapper mapper) {
        return mapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void assertDto(ScheduleDTO expected, ScheduleDTO actual) {
        assertDto(expected, actual, excludes);
    }

    public static void assertDto(ScheduleDTO expected, ScheduleDTO actual, List<ScheduleField> excludes) {
        for (ScheduleField field : ScheduleField.values()) {
            if (!excludes.contains(field)) {
                String errorMsg = field.name() + " does not satisfy criteria";
                switch (field) {
                    case ID:
                        assertEquals(expected.getId(), actual.getId(), errorMsg);
                        break;
                    case EMPLOYEES:
                        assertEquals(expected.getEmployeeIds(), actual.getEmployeeIds(), errorMsg);
                        break;
                    case PETS:
                        assertEquals(expected.getPetIds(), actual.getPetIds(), errorMsg);
                        break;
                    case DATE:
                        assertEquals(expected.getDate(), actual.getDate(), errorMsg);
                        break;
                    case ACTIVITIES:
                        assertEquals(expected.getActivities(), actual.getActivities(), errorMsg);
                        break;
                }
            }
        }
    }
}
