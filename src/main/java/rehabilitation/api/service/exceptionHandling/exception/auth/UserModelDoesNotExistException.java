package rehabilitation.api.service.exceptionHandling.exception.auth;

public class UserModelDoesNotExistException extends RuntimeException{
    public UserModelDoesNotExistException() {
        super("User of this token does not exists anymore");
    }
}
