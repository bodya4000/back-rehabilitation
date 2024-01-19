package rehabilitation.api.service.business.businessServices.specialistBusiness.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessServices.abstractions.ModelViewService;
import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.*;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistViewService extends ModelViewService<SpecialistModel, SpecialistDto> {

    private final SpecialistRepository specialistRepository;

    @Override
    public SpecialistDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        var specialistModel = getModelIfExists(login, specialistRepository);
        List<String> listOfClientsLogin = specialistModel.getListOfClientsLogin();
        return doMapModelDtoAndGet(specialistModel, listOfClientsLogin);
    }

    @Override
    public List<SpecialistDto> getListOfModelDto() {
        List<SpecialistModel> specialistModels = specialistRepository.findAllBy();
        return specialistModels.stream().map(specialistModel -> {
            List<String> listOfClientsLogin = specialistModel.getClients().stream().map(ClientModel::getLogin).collect(Collectors.toList());
            return doMapModelDtoAndGet(specialistModel, listOfClientsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    protected SpecialistDto doMapModelDtoAndGet(UserModel userModel, List<String> listOfClientsLogin) {
        var specialistModel = (SpecialistModel) userModel;
        return new SpecialistDto(
                specialistModel.getLogin(), specialistModel.getFirstName(), specialistModel.getLastName(),
                specialistModel.getCity(), specialistModel.getAge(), specialistModel.getExperience(),
                specialistModel.getRate(), specialistModel.getType(),
                specialistModel.getImgUrl(), specialistModel.getDescription(),
                specialistModel.getReHub() != null ? specialistModel.getReHub().getLogin() : "",
                listOfClientsLogin);
    }
}
