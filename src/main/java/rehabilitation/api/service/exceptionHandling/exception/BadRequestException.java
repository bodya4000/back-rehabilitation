package rehabilitation.api.service.exceptionHandling.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String s){
        super(s);
    }
}
