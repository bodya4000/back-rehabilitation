package veres.lection.first.rest.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ClientModel;
import veres.lection.first.rest.model.SpecialistModel;
import veres.lection.first.rest.repositories.ClientRepository;
import veres.lection.first.rest.repositories.SpecialistRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private ClientRepository clientRepository;

    public SpecialistModel saveRehabilitationSpecialist(SpecialistModel specialistModel) {
        return specialistRepository.save(specialistModel);
    }
    public List<SpecialistModel> getAllRehabilitationSpecialists() {
        return specialistRepository.findAll();
    }

    public SpecialistModel getById(int id) throws NotFoundIdException {
        return Optional.of(specialistRepository.findById(id))
                .get()
                .orElseThrow(() -> new NotFoundIdException("Cannot find id - " + id));
    }

    public void updateRehabilitationSpecialist(int id, Map<String, Object> updates) throws NotFoundIdException {
        var currentSpecialist = getById(id);
        executeUpdates(updates, currentSpecialist);
        saveRehabilitationSpecialist(currentSpecialist);
    }

    public void removeClientById( SpecialistModel specialist, ClientModel client) {
        specialist.getClients().remove(client);
    }

    public void addClientById(SpecialistModel specialist, ClientModel client) {
        specialist.getClients().add(client);
    }


    public void deleteRehabilitationSpecialists(int id) throws NotFoundIdException {
        specialistRepository.delete(getById(id));
    }





    private void executeUpdates(Map<String, Object> updates, SpecialistModel currentSpecialist) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    currentSpecialist.setFirstName((String) value);
                case "lastName":
                    currentSpecialist.setLastName((String) value);
                case "login":
                    currentSpecialist.setLogin((String) value);
                case "type":
                    currentSpecialist.setType((String) value);
                case "phoneNumber":
                    currentSpecialist.setPhoneNumber((String) value);
                case "email":
                    currentSpecialist.setEmail((String) value);
                case "experience":
                    currentSpecialist.setExperience((Integer) value);
                case "rate":
                    currentSpecialist.setRate((Integer) value);
                case "description":
                    currentSpecialist.setDescription((String) value);
            }
        });
    }

}
