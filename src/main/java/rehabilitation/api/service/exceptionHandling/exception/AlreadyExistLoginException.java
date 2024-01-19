package rehabilitation.api.service.exceptionHandling.exception;

import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.entity.SpecialistModel;

public class AlreadyExistLoginException extends Exception{
    public AlreadyExistLoginException(String login) {
        super("there is already exists " + login);
    }

    public AlreadyExistLoginException(SpecialistModel specialistModel) {
        super("Already exists " + specialistModel.getLogin());
    }

    public AlreadyExistLoginException(ClientModel clientModel) {
        super("there is already exists " + clientModel.getLogin());
    }

    public AlreadyExistLoginException(ReHubModel reHubModel) {
        super("there is already exists " + reHubModel.getLogin());
    }

}
