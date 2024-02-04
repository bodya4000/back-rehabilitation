package rehabilitation.api.service.business.businessServices.abstractions;

import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

import java.util.List;

public abstract class ModelViewService<ModelDto> {


    public abstract ModelDto getModelDtoByLogin(String login) throws NotFoundLoginException;

    public abstract List<ModelDto> getListOfModelDto();
}
