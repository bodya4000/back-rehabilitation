package rehabilitation.api.service.exceptionHandling.exception;

public class PasswordRegistryException extends Exception{

    public PasswordRegistryException(){
        super("Passwords don't match");
    }
}
