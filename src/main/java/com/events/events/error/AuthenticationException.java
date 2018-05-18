package com.events.events.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AuthenticationException extends RuntimeException {

    private String message;

    public AuthenticationException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
