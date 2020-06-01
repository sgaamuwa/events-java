package com.events.events.error;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> constructHandleException(RuntimeException e, WebRequest request, HttpStatus status){
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(status, e.getMessage());
        return handleExceptionInternal(e, customErrorMessage, new HttpHeaders(), customErrorMessage.getStatus(), request);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(RuntimeException e, WebRequest request){
        return constructHandleException(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalFriendActionException.class)
    protected ResponseEntity<Object> handleDuplicateCreationException(RuntimeException e, WebRequest request){
        return constructHandleException(e, request, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, InvalidDateException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(RuntimeException e, WebRequest request){
        return constructHandleException(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyListException.class)
    protected ResponseEntity<Object> handleEmptyListException(RuntimeException e, WebRequest request){
        return constructHandleException(e, request, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(RuntimeException e, WebRequest request){
        return constructHandleException(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorisationException.class)
    protected ResponseEntity<Object> handleAuthorisationException(RuntimeException  e, WebRequest request){
        return constructHandleException(e, request, HttpStatus.UNAUTHORIZED);
    }



}
