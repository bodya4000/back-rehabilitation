package rehabilitation.api.service.exceptionHandling.exception;

public class WrongPasswordOrLoginException extends Exception{
    public WrongPasswordOrLoginException(String e){
        super("Wrong password or login" + ": " + e);
    }
}
