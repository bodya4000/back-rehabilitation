package rehabilitation.api.service.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SpecialistRepositoryImpl implements CustomSpecRepo{

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public SpecialistDto testDto(String login) {
        SpecialistModel specialist = entityManager.createQuery(
                        "select s from SpecialistModel s " +
                                "left join fetch s.reHub " +
                                "left join fetch s.clients c " +
                                "where s.login = :login", SpecialistModel.class)
                .setParameter("login", login)
                .getSingleResult();

        List<String> clientLogins = specialist.getClients().stream()
                .map(ClientModel::getLogin)
                .collect(Collectors.toList());

        return new SpecialistDto(
                specialist.getLogin(),
                specialist.getFirstName(),
                specialist.getLastName(),
                specialist.getExperience(),
                specialist.getRate(),
                specialist.getType(),
                specialist.getImgUrl(),
                specialist.getDescription(),
                specialist.getReHub().getLogin(),
                clientLogins
        );
    }


    @Override
    public List<SpecialistModel> findAllBy() {
        return null;
    }

    @Override
    public Optional<SpecialistModel> findByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public boolean existsByLogin(String login) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
