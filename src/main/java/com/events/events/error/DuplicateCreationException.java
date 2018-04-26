package com.events.events.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateCreationException extends RuntimeException {

    private String message;

    public DuplicateCreationException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
