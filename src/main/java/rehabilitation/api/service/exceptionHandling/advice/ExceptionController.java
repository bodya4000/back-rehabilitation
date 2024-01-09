package rehabilitation.api.service.exceptionHandling.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import rehabilitation.api.service.dto.ExceptionDto;
import rehabilitation.api.service.exceptionHandling.exception.*;

import java.util.Date;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundLoginException.class)
    public ResponseEntity<?> notFoundLoginHandler(NotFoundLoginException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(
                        e.getMessage(),
                        new Date()
                ));
    }


    @ExceptionHandler(AlreadyExistLoginException.class)
    public ResponseEntity<?> sameLoginHandler(AlreadyExistLoginException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(
                        e.getMessage(),
                        new Date()
                ));
    }

    @ExceptionHandler(WrongPasswordOrLoginException.class)
    public ResponseEntity<?> wrongCredentialsHandler(WrongPasswordOrLoginException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionDto(
                        e.getMessage(),
                        new Date()
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestHandler(BadRequestException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDto(
                        e.getMessage(),
                        new Date()
                ));
    }

    @ExceptionHandler(PasswordRegistryException.class)
    public ResponseEntity<?> badRequestHandler(PasswordRegistryException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDto(
                        e.getMessage(),
                        new Date()
                ));
    }
}
