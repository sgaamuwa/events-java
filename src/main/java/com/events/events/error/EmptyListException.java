package com.events.events.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class EmptyListException extends RuntimeException {

    private String message;

    public EmptyListException(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
