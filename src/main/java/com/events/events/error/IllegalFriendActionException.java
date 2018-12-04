package com.events.events.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class IllegalFriendActionException extends RuntimeException {

    private String message;

    public IllegalFriendActionException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
