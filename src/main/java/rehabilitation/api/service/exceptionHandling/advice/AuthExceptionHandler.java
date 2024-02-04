package rehabilitation.api.service.exceptionHandling.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import rehabilitation.api.service.dto.ExceptionDto;
import rehabilitation.api.service.exceptionHandling.exception.auth.InvalidTokenException;
import rehabilitation.api.service.exceptionHandling.exception.auth.TokenExpiredException;
import rehabilitation.api.service.exceptionHandling.exception.auth.WrongPasswordOrLoginException;

import java.util.Date;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredExceptionHandler(TokenExpiredException e){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionDto(
                        e.getMessage(),
                        new Date()
                ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> invalidTokenExceptionHandler(TokenExpiredException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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
}
