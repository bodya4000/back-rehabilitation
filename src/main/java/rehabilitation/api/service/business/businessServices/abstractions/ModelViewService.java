package rehabilitation.api.service.business.businessServices.abstractions;

import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.CommonRepository;

import java.util.List;

public abstract class ModelViewService<Model extends UserModel, ModelDto> {


    public abstract ModelDto getModelDtoByLogin(String login) throws NotFoundLoginException;

    public abstract List<ModelDto> getListOfModelDto();

    protected abstract ModelDto doMapModelDtoAndGet(UserModel userModel, List<String> listOfChildLogins);
}
