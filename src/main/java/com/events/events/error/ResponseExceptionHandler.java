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

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(RuntimeException e, WebRequest request){
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
        return handleExceptionInternal(e, customErrorMessage.getMessage(), new HttpHeaders(), customErrorMessage.getStatus(), request);
    }

    @ExceptionHandler(IllegalFriendActionException.class)
    protected ResponseEntity<Object> handleDuplicateCreationException(RuntimeException e, WebRequest request){
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        return handleExceptionInternal(e, customErrorMessage, new HttpHeaders(), customErrorMessage.getStatus(), request);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, InvalidDateException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(RuntimeException e, WebRequest request){
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(HttpStatus.BAD_REQUEST, e.getMessage());
        return handleExceptionInternal(e, customErrorMessage.getMessage(), new HttpHeaders(), customErrorMessage.getStatus(), request);
    }

    @ExceptionHandler(EmptyListException.class)
    protected ResponseEntity<Object> handleEmptyListException(RuntimeException e, WebRequest request){
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(HttpStatus.NO_CONTENT, e.getMessage());
        return handleExceptionInternal(e, customErrorMessage.getMessage(), new HttpHeaders(), customErrorMessage.getStatus(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(RuntimeException e, WebRequest request){
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(HttpStatus.BAD_REQUEST, e.getMessage());
        return handleExceptionInternal(e, customErrorMessage.getMessage(), new HttpHeaders(), customErrorMessage.getStatus(), request);
    }

}
