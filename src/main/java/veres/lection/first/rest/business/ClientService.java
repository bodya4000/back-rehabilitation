package veres.lection.first.rest.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ClientModel;
import veres.lection.first.rest.model.SpecialistModel;
import veres.lection.first.rest.repositories.ClientRepository;
import veres.lection.first.rest.repositories.SpecialistRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    public void saveClient(ClientModel clientModel) {
//        clientModel.getSpecialistModel().addClient(clientModel);
        clientRepository.save(clientModel);
    }
    public List<ClientModel> getClientsList() {
        return clientRepository.findAll();
    }

    public void changeClient(int id, Map<String, Object> updates) throws NotFoundIdException {
        var currentClient = getClientById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName": currentClient.setFirstName((String) value);
                case "lastName": currentClient.setLastName((String) value);
                case "login": currentClient.setLogin((String) value);
                case "address": currentClient.setAddress((String) value);
                case "email": currentClient.setEmail((String) value);
                case "phoneNumber": currentClient.setPhoneNumber((String) value);
                case "healthState": currentClient.setHealthState((String) value);
                case "specialistModel":
                    int clientModelId = (int) value;
                    try {
                        SpecialistModel specialistModel = specialistRepository.findById(clientModelId)
                                .orElseThrow(() -> new NotFoundIdException("Cannot find specialist with id - " + clientModelId));
                        currentClient.setSpecialistModel(specialistModel);
                    } catch (NotFoundIdException e) {
                        throw new RuntimeException(e);
                    }
                    currentClient.setSpecialistModel((SpecialistModel) value);
            }
        });

        saveClient(currentClient);
    }

    public void deleteClient(int id) throws NotFoundIdException {
        clientRepository.delete(getClientById(id));
    }

    public ClientModel getClientById(int id) throws NotFoundIdException {
        return Optional.of(clientRepository.findById(id))
                .get()
                .orElseThrow(() -> new NotFoundIdException("Cannot find id - " + id));
    }
}
