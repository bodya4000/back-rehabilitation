package rehabilitation.api.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.exception.NotFoundIdException;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReHubService {
    @Autowired
    private ReHubRepository reHubRepository;
    @Autowired
    private SpecialistRepository specialistRepository;

    @Transactional(readOnly = true)
    public ReHubModel getById(String login) throws NotFoundIdException {
        return reHubRepository.findByLogin(login).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<ReHubModel> getAll() {
        return reHubRepository.findAllBy().orElseThrow();
    }

    @Transactional
    public void save(ReHubModel reHubModel) {
        reHubModel.getSpecialists().forEach(specialistModel -> {
            var real = specialistRepository.findByLogin(specialistModel.getLogin()).orElseThrow();
            reHubModel.addSpecialist(real);
            real.setReHub(reHubModel);
        });
    }

    @Transactional
    public void updateRehub(String login, Map<String, Object> updates) throws NotFoundIdException {
        var reHub = reHubRepository.findByLogin(login).orElseThrow();

        updates.forEach((key, value) -> {
            switch (key) {
                case "name": reHub.setName((String) value);
                case "location": reHub.setLocation((String) value);
                case "contact_information": reHub.setContactInformation((String) value);
            }
        });
    }

    @Transactional
    public void delete(ReHubModel reHubModel) {
        reHubRepository.delete(reHubModel);
    }

    @Transactional
    public void removeSpecialist(
            String reHubId,
            String specialistLogin) {
        var reHub = reHubRepository.findByLogin(reHubId).orElseThrow();
        var specialist = specialistRepository.findByLogin(specialistLogin).orElseThrow();
        reHub.removeSpecialist(specialist);
    }
    @Transactional
    public void addSpecialistById(String reHubId, String specialistLogin) {
        var reHub = reHubRepository.findByLogin(reHubId).orElseThrow();
        var specialist = specialistRepository.findByLogin(specialistLogin).orElseThrow();
        reHub.addSpecialist(specialist);
    }
}
