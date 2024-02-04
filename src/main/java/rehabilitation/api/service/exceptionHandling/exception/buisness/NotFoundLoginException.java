package rehabilitation.api.service.exceptionHandling.exception.buisness;

public class NotFoundLoginException extends Exception {

    public NotFoundLoginException(String login){
        super("Cannot find in data base login " + login);
    }
}
