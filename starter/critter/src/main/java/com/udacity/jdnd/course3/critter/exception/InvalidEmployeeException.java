package com.udacity.jdnd.course3.critter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid employee")
public class InvalidEmployeeException extends AbstractException {

    public InvalidEmployeeException() {
        super();
    }

    public InvalidEmployeeException(String message) {
        super(message);
    }

    public InvalidEmployeeException(List<String> messages) {
        super(messages);
    }

    public InvalidEmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
