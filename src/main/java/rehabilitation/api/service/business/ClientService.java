package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;

import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;

import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.repositories.SpecialistRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("clientService")
@RequiredArgsConstructor
public class ClientService extends CommonService<ClientModel, ClientDto> {

    private final ClientRepository clientRepository;

    private final SpecialistRepositoryImpl specialistRepository;
//
//    @Autowired
//    public void setClientRepository(ClientRepository clientRepository) {
//        this.clientRepository = clientRepository;
//    }

//    @Autowired
//    public void setSpecialistRepository(SpecialistRepository specialistRepository) {
//        this.specialistRepository = specialistRepository;
//    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getAllModelView() {
        List<ClientModel> clientModels = clientRepository.findAllBy();
        return clientModels.stream().map(clientModel -> {
            List<String> listOfClientsLogin = clientModel.getSpecialists().stream().map(SpecialistModel::getLogin).collect(Collectors.toList());
            return doMapModelDtoAndGet(clientModel, listOfClientsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto getModelViewByLogin(String login) throws NotFoundLoginException {
        var clientModel = checkIfBaseHasLogin(login, clientRepository);
        List<String> listOfClientsLogin = clientModel.getSpecialists().stream().map(SpecialistModel::getLogin).collect(Collectors.toList());
        return doMapModelDtoAndGet(clientModel, listOfClientsLogin);
    }

    @Override
    @Transactional
    public void saveModel(ClientModel clientModel) throws AlreadyExistLoginException {
        // todo check if exists injected specialists
        if (checkIfBaseHasModel(clientModel, clientRepository)) {
            clientRepository.save(clientModel);
        }
    }

    @Override
    public void deleteModel(String login) throws NotFoundLoginException {
        var client = checkIfBaseHasLogin(login, clientRepository);
        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public void addChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        var client = checkIfBaseHasLogin(clientLogin, clientRepository);
        var specialist = checkIfBaseHasLogin(specialistLogin, specialistRepository);
        client.addSpecialist(specialist);
    }

    @Override
    @Transactional
    public void removeChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        ClientModel client = checkIfBaseHasLogin(clientLogin, clientRepository);
        SpecialistModel specialist = checkIfBaseHasLogin(specialistLogin, specialistRepository);
        client.removeSpecialist(specialist);
    }

    @Override
    ClientDto doMapModelDtoAndGet(ClientModel clientModel, List<String> listOfSpecialistLogin) {
        return new ClientDto(clientModel.getLogin(), clientModel.getFirstName(),
                clientModel.getLastName(), clientModel.getEmail(),
                clientModel.getAddress(), clientModel.getContactInformation(), clientModel.getImgUrl(), listOfSpecialistLogin);
    }

    @Override
    void executeUpdates(Map<String, Object> updates, ClientModel currentClient) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    if (value instanceof String) {
                        currentClient.setFirstName((String) value);
                    }
                    break;
                case "lastName":
                    if (value instanceof String) {
                        currentClient.setLastName((String) value);
                    }
                    break;
                case "email":
                    if (value instanceof String) {
                        currentClient.setEmail((String) value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key " + key);
            }
        });
    }

    @Override
    @Transactional
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var currentClient = checkIfBaseHasLogin(login, clientRepository);
        executeUpdates(updates, currentClient);
    }
}


