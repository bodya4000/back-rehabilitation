package veres.lection.first.rest.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ClientModel;
import veres.lection.first.rest.model.SpecialistModel;
import veres.lection.first.rest.repositories.SpecialistRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;

    public void saveRehabilitationSpecialist(SpecialistModel specialistModel) {
        System.out.println(specialistModel);
        specialistRepository.save(specialistModel);
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
        System.out.println(updates);

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
                case "clients":
                    currentSpecialist.setClients((Set<ClientModel>) value);

            }
        });
        saveRehabilitationSpecialist(currentSpecialist);
    }

    public void deleteRehabilitationSpecialists(int id) throws NotFoundIdException {
        specialistRepository.delete(getById(id));
    }


}
