package com.udacity.jdnd.course3.critter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No entity found")
public class NoEntityResultException extends AbstractException {

    public NoEntityResultException() {
        super();
    }

    public NoEntityResultException(String message) {
        super(message);
    }

    public NoEntityResultException(List<String> messages) {
        super(messages);
    }

    public NoEntityResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
