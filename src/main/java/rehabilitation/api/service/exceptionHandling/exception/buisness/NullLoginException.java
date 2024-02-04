package rehabilitation.api.service.exceptionHandling.exception.buisness;

public class NullLoginException extends Exception{
    public NullLoginException() {
        super("login cannot be null");
    }

}
