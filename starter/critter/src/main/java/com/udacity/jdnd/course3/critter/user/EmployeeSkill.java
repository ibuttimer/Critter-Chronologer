package com.udacity.jdnd.course3.critter.user;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * A example list of employee skills that could be included on an employee or a schedule request.
 */
public enum EmployeeSkill {
    PETTING, WALKING, FEEDING, MEDICATING, SHAVING;

    public static Optional<EmployeeSkill> from(String skill) {
        Optional<EmployeeSkill> result = Optional.empty();
        if (StringUtils.hasText(skill)) {
            result = Arrays.stream(values())
                    .filter(v -> v.name().equals(skill))
                    .findFirst();
        }
        return result;
    }

}
