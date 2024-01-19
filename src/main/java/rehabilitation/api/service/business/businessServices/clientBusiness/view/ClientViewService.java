package rehabilitation.api.service.business.businessServices.clientBusiness.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelViewService;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.List;
import java.util.stream.Collectors;

import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.getModelIfExists;

@Service
@RequiredArgsConstructor
public class ClientViewService extends ModelViewService<ClientModel, ClientDto> {

    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public ClientDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        var clientModel = getModelIfExists(login, clientRepository);
        List<String> listOfClientsLogin = clientModel.getListOfSpecialistsLogin();
        return doMapModelDtoAndGet(clientModel, listOfClientsLogin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getListOfModelDto() {
        List<ClientModel> clientModels = clientRepository.findAllBy();
        return clientModels.stream().map(specialistModel -> {
            List<String> listOfClientsLogin = specialistModel.getListOfSpecialistsLogin();
            return doMapModelDtoAndGet(specialistModel, listOfClientsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    protected ClientDto doMapModelDtoAndGet(UserModel userModel, List<String> listOfClientsLogin) {
        var clientModel = (ClientModel) userModel;
        return new ClientDto(
                clientModel.getLogin(), clientModel.getFirstName(), clientModel.getLastName(),
                clientModel.getEmail(), clientModel.getAddress(), clientModel.getContactInformation(),
                clientModel.getImgUrl(),
                listOfClientsLogin);
    }
}
