package rehabilitation.api.service.business.businessServices.reHubBusiness.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelViewService;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.dto.RehubDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.ReHubRepository;

import java.util.List;
import java.util.stream.Collectors;

import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.getModelIfExists;

@Service
@RequiredArgsConstructor
public class ReHubViewService extends ModelViewService<ReHubModel, RehubDto> {

    private final ReHubRepository reHubRepository;

    @Override
    @Transactional(readOnly = true)
    public RehubDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        var reHubModel = getModelIfExists(login, reHubRepository);
        List<String> listOfSpecialistsLogin = reHubModel.getListOfSpecialistsLogin();
        return doMapModelDtoAndGet(reHubModel, listOfSpecialistsLogin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RehubDto> getListOfModelDto() {
        List<ReHubModel> reHubModels = reHubRepository.findAllBy();
        return reHubModels.stream().map(specialistModel -> {
            List<String> listOfSpecialistsLogin = specialistModel.getListOfSpecialistsLogin();
            return doMapModelDtoAndGet(specialistModel, listOfSpecialistsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    protected RehubDto doMapModelDtoAndGet(UserModel userModel, List<String> listOfSpecialistLogin) {
        var reHubModel = (ReHubModel) userModel;
        return new RehubDto(
                reHubModel.getLogin(), reHubModel.getName(), reHubModel.getEmail(),
                reHubModel.getAddress(), reHubModel.getContactInformation(),
                reHubModel.getImgUrl(),
                listOfSpecialistLogin);
    }
}
