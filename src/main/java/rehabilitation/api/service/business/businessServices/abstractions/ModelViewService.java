package rehabilitation.api.service.business.businessServices.abstractions;

import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;

import java.util.List;

public abstract class ModelViewService<Model extends UserModel, ModelDto> {


    public abstract ModelDto getModelDtoByLogin(String login) throws NotFoundLoginException;

    public abstract List<ModelDto> getListOfModelDto();
}
