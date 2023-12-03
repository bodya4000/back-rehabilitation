package veres.lection.first.rest.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ReHubModel;
import veres.lection.first.rest.model.SpecialistModel;
import veres.lection.first.rest.repositories.ReHubRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReHubService {
    @Autowired
    private ReHubRepository reHubRepository;

    public ReHubModel getById(int id) throws NotFoundIdException {
        return Optional.of(reHubRepository.findById(id))
                .get()
                .orElseThrow(() -> new NotFoundIdException("Cannot find id - " + id));
    }


    public List<ReHubModel> getAll() {
        return reHubRepository.findAll();
    }

    public void persist(ReHubModel reHubModel) {
        reHubRepository.save(reHubModel);
    }

    public List<ReHubModel> getSpecialists() {
        return reHubRepository.findAll();
    }

    public void update(int id, Map<String, Object> updates) throws NotFoundIdException {
        var reHub = getById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name": reHub.setName((String) value);
                case "location": reHub.setLocation((String) value);
                case "contact_information": reHub.setContactInformation((String) value);
            }
        });

        persist(reHub);
    }

    public void delete(ReHubModel reHubModel) {
        reHubRepository.delete(reHubModel);
    }

    public void removeSpecialistById(
            ReHubModel reHubModel,
            SpecialistModel specialist) {
        reHubModel.getSpecialists().remove(specialist);
    }
    public void addSpecialistById(ReHubModel reHubModel, SpecialistModel specialist) {
        reHubModel.getSpecialists().add(specialist);
    }
}
