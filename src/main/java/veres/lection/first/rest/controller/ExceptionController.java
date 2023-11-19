package veres.lection.first.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import veres.lection.first.rest.exception.NotFoundIdException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundIdException.class)
    public ResponseEntity<String> handler(NotFoundIdException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

}
