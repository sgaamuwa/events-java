package com.events.events.error;

import org.springframework.http.HttpStatus;

import java.util.List;

public class CustomErrorMessage {

    private HttpStatus status;
    private String message;
    private String errors;

    public CustomErrorMessage(HttpStatus status, String message, String errors){
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
