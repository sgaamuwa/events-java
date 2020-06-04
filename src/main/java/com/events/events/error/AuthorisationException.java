package com.events.events.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthorisationException extends RuntimeException{
    private String message;

    public AuthorisationException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
