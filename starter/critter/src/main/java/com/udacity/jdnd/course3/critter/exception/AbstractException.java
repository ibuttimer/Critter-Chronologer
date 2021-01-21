package com.udacity.jdnd.course3.critter.exception;

import com.google.common.base.Joiner;

import java.util.List;

public abstract class AbstractException extends RuntimeException {

    public AbstractException() {
        super();
    }

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(List<String> messages) {
        super(Joiner.on("\n").join(messages));
    }

    public AbstractException(String message, Throwable cause) {
        super(message, cause);
    }
}
