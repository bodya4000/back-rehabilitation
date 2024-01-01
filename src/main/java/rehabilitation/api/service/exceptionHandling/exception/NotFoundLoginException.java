package rehabilitation.api.service.exceptionHandling.exception;

import rehabilitation.api.service.entity.BaseModel;

public class NotFoundLoginException extends Exception {

    public NotFoundLoginException(String login){
        super("Cannot find in data base login " + login);
    }
}
