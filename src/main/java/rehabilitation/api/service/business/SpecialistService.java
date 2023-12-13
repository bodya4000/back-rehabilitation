package rehabilitation.api.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.exception.NotFoundIdException;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private ClientRepository clientRepository;

    /* get specialist dto by login for controller*/
    @Transactional(readOnly = true)
    public SpecialistDto getSpecialistView(String login) {
        var specialistModel = specialistRepository.findByLogin(login).orElseThrow();
        List<String> listOfClientsLogin = specialistModel.getClients().stream().map(ClientModel::getLogin).collect(Collectors.toList());
        return doMapSpecialistDtoAndGet(specialistModel, listOfClientsLogin);
    }

    /* get specialist dto by login for controller*/
    @Transactional(readOnly = true)
    public List<SpecialistDto> getAllSpecialistView() {
        List<SpecialistModel> specialistModels = specialistRepository.getAllBy();
        return specialistModels.stream().map(specialistModel -> {
            List<String> listOfClientsLogin = specialistModel.getClients().stream().map(ClientModel::getLogin).collect(Collectors.toList());
            return doMapSpecialistDtoAndGet(specialistModel, listOfClientsLogin);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void saveSpecialist(SpecialistModel specialist) {
//        for(ClientModel nonExistingClient: specialist.getClients()) {
//            ClientModel existingClient = clientRepository.findByLogin(nonExistingClient.getLogin());
//            existingClient.addSpecialist(specialist);
//        }
        specialist.getClients().stream()
                .map(client -> clientRepository.findByLogin(client.getLogin()))
                .forEach(client -> client.addSpecialist(specialist));

        specialistRepository.save(specialist);
    }

    @Transactional
    public void updateSpecialist(String login, Map<String, Object> updates) {
        var specialist = specialistRepository.findByLogin(login).orElseThrow();
        executeUpdates(updates, specialist);
    }

    @Transactional
    public void deleteSpecialist(String login) throws NotFoundIdException {
        SpecialistModel specialist = specialistRepository.findByLogin(login).orElseThrow();
        specialistRepository.delete(specialist);
    }

    @Transactional
    public void addClient(String specialistLogin, String clientLogin) {
        SpecialistModel specialistModel = specialistRepository.findByLogin(specialistLogin).orElseThrow();
        ClientModel clientModel = clientRepository.findByLogin(clientLogin);
        specialistModel.addClient(clientModel);
    }

    @Transactional
    public void removeClient(String specialistLogin, String clientLogin) {
        SpecialistModel specialistModel = specialistRepository.findByLogin(specialistLogin).orElseThrow();
        ClientModel clientModel = clientRepository.findByLogin(clientLogin);
        specialistModel.removeClient(clientModel);
    }

    private void executeUpdates(Map<String, Object> updates, SpecialistModel currentSpecialist) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    if (value instanceof String) {
                        currentSpecialist.setFirstName((String) value);
                    }
                    break;
                case "lastName":
                    if (value instanceof String) {
                        currentSpecialist.setLastName((String) value);
                    }
                    break;
                case "type":
                    if (value instanceof String) {
                        currentSpecialist.setType((String) value);
                    }
                    break;
                case "phoneNumber":
                    if (value instanceof String) {
                        currentSpecialist.setPhoneNumber((String) value);
                    }
                    break;
                case "email":
                    if (value instanceof String) {
                        currentSpecialist.setEmail((String) value);
                    }
                    break;
                case "description":
                    if (value instanceof String) {
                        currentSpecialist.setDescription((String) value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key " + key);
            }
        });
    }

    private SpecialistDto doMapSpecialistDtoAndGet(SpecialistModel specialistModel, List<String> listOfClientsLogin) {
        return new SpecialistDto(
                specialistModel.getLogin(),
                specialistModel.getFirstName(),
                specialistModel.getLastName(),
                specialistModel.getExperience(),
                specialistModel.getRate(),
                specialistModel.getType(),
                specialistModel.getImgUrl(),
                specialistModel.getDescription(),
                specialistModel.getReHub() != null ? specialistModel.getReHub().getLogin() : "", listOfClientsLogin);
    }
}
