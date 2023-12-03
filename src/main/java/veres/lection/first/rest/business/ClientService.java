package veres.lection.first.rest.business;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import veres.lection.first.rest.exception.AlreadyExistObjectException;
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

    public ClientModel saveClient(ClientModel clientModel) {
        return clientRepository.save(clientModel);
    }
    public List<ClientModel> getClientsList() {
        return clientRepository.findAll();
    }

    public void changeClient(int id, Map<String, Object> updates) throws NotFoundIdException {
        var currentClient = getClientById(id);

        executeUpdates(updates, currentClient);

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

    public void removeSpecialistById(ClientModel client, SpecialistModel specialist) {
        client.getSpecialists().remove(specialist);
    }

    public void addSpecialist(ClientModel client, SpecialistModel specialist) {
        client.getSpecialists().add(specialist);
    }

    private void executeUpdates(Map<String, Object> updates, ClientModel currentClient) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName": currentClient.setFirstName((String) value);
                case "lastName": currentClient.setLastName((String) value);
                case "login": currentClient.setLogin((String) value);
                case "address": currentClient.setAddress((String) value);
                case "email": currentClient.setEmail((String) value);
                case "phoneNumber": currentClient.setPhoneNumber((String) value);
                case "healthState": currentClient.setHealthState((String) value);
            }
            clientRepository.save(currentClient);
        });
    }
}
