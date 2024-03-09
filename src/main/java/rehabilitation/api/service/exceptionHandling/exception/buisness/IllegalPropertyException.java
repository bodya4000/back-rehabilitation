package rehabilitation.api.service.exceptionHandling.exception.buisness;

public class IllegalPropertyException extends RuntimeException{
    public IllegalPropertyException(String property) {
        super("Cannot change property " + property);
    }
}
