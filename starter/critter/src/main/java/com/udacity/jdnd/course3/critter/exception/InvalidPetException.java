package com.udacity.jdnd.course3.critter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid pet")
public class InvalidPetException extends AbstractException {

    public InvalidPetException() {
        super();
    }

    public InvalidPetException(String message) {
        super(message);
    }

    public InvalidPetException(List<String> messages) {
        super(messages);
    }

    public InvalidPetException(String message, Throwable cause) {
        super(message, cause);
    }
}
