package com.events.events.error;

public class AuthenticationException extends RuntimeException {

    private String message;

    public AuthenticationException(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
