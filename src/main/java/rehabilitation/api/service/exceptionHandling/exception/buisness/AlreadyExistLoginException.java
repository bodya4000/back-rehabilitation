package rehabilitation.api.service.exceptionHandling.exception.buisness;

import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;

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
