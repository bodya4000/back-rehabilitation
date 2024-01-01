package rehabilitation.api.service.exceptionHandling.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundLoginException.class)
    public ResponseEntity<String> notFoundLoginHandler(NotFoundLoginException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }


    @ExceptionHandler(AlreadyExistLoginException.class)
    public ResponseEntity<String> sameObjectHandler(AlreadyExistLoginException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

}
