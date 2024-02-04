package rehabilitation.api.service.exceptionHandling.exception.auth;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(){
        super("Invalid token");
    }
}
