package com.udacity.jdnd.course3.critter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid schedule")
public class InvalidScheduleException extends AbstractException {

    public InvalidScheduleException() {
        super();
    }

    public InvalidScheduleException(String message) {
        super(message);
    }

    public InvalidScheduleException(List<String> messages) {
        super(messages);
    }

    public InvalidScheduleException(String message, Throwable cause) {
        super(message, cause);
    }
}
