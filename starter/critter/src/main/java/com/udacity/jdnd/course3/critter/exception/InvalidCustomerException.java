package com.udacity.jdnd.course3.critter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid customer")
public class InvalidCustomerException extends AbstractException {

    public InvalidCustomerException() {
        super();
    }

    public InvalidCustomerException(String message) {
        super(message);
    }

    public InvalidCustomerException(List<String> messages) {
        super(messages);
    }

    public InvalidCustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
