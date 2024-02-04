package rehabilitation.api.service.exceptionHandling.exception.auth;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String s){
        super(s);
    }
}
