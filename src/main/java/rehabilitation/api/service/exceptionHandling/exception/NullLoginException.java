package rehabilitation.api.service.exceptionHandling.exception;

public class NullLoginException extends Exception{
    public NullLoginException() {
        super("login cannot be null");
    }

}
