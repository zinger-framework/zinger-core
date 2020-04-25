package com.food.ordering.zinger.interceptor;

import com.food.ordering.zinger.constant.ErrorLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InvalidExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleInvalidFieldException(InvalidException exception){
        if(exception.getMessage().equals(ErrorLog.InvalidHeader))
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.PRECONDITION_FAILED);
        else if(exception.getMessage().equals(ErrorLog.UnAuthorizedAccess))
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.PRECONDITION_REQUIRED);
    }
}
