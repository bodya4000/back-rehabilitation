package rehabilitation.api.service.exceptionHandling.exception.buisness;

public class NotFoundLoginException extends Exception {

    public NotFoundLoginException(String login){
        super("Cannot find in data base login " + login);
    }

    public NotFoundLoginException(String firstLogin, String secondLogin){
        super("Cannot find in data base login " + firstLogin + " or " + secondLogin);
    }

    public NotFoundLoginException() {
        super("Cannot find login");
    }
}
