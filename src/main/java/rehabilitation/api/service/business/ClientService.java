package rehabilitation.api.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.exception.NotFoundIdException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;

    @Transactional(readOnly = true)
    public List<ClientDto> getAllClientsView() {
        List<ClientModel> clientModels = clientRepository.findAllBy();
        return clientModels.stream()
                .map(clientModel -> {
                    List<String> listOfClientsLogin = clientModel.getSpecialists().stream()
                            .map(SpecialistModel::getLogin)
                            .collect(Collectors.toList());
                    return doMapClientDtoAndGet(clientModel, listOfClientsLogin);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientDto getClientById(String login) throws NotFoundIdException {
        var clientModel = clientRepository.findByLogin(login);
        List<String> listOfClientsLogin = clientModel.getSpecialists().stream()
                .map(SpecialistModel::getLogin)
                .collect(Collectors.toList());
        return doMapClientDtoAndGet(clientModel, listOfClientsLogin);
    }

    @Transactional
    public void saveClient(ClientModel clientModel) {
        clientRepository.save(clientModel);
    }

    @Transactional
    public void updateClient(String login, Map<String, Object> updates) throws NotFoundIdException {
        var currentClient = clientRepository.findByLogin(login);
        executeUpdates(updates, currentClient);
    }

    @Transactional
    public void deleteClient(String login) throws NotFoundIdException {
        var client = clientRepository.findByLogin(login);
        clientRepository.delete(client);
    }

    @Transactional
    public void addSpecialist(String clientLogin, String specialistLogin) {
        var client = clientRepository.findByLogin(clientLogin);
        var specialist = specialistRepository.findByLogin(specialistLogin).orElseThrow();
        client.addSpecialist(specialist);
    }

    @Transactional
    public void removeSpecialistById(String clientLogin, String specialistLogin) {
        var client = clientRepository.findByLogin(clientLogin);
        var specialist = specialistRepository.findByLogin(specialistLogin).orElseThrow();
        client.removeSpecialist(specialist);
    }

    private void executeUpdates(Map<String, Object> updates, ClientModel currentClient) {
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

    private ClientDto doMapClientDtoAndGet(ClientModel clientModel, List<String> listOfSpecialistLogin) {
        System.out.println(listOfSpecialistLogin);
        return new ClientDto(
                clientModel.getLogin(),
                clientModel.getFirstName(),
                clientModel.getLastName(),
                clientModel.getEmail(),
                clientModel.getAddress(),
                clientModel.getPhoneNumber(),
                listOfSpecialistLogin
        );
    }
}
