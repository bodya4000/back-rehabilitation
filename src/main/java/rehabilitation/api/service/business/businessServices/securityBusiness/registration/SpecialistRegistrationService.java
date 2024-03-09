package rehabilitation.api.service.business.businessServices.securityBusiness.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessUtils.ESUtils;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.repositories.elasticsearch.SpecialistSearchRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class SpecialistRegistrationService implements Registration{
    private final SpecialistSearchRepository specialistSearchRepository;
    private final SpecialistRepository specialistRepository;
    private final ChatUserRepository chatUserRepository;
    private final RegistrationUtil registrationUtil;

    @Override
    @Transactional
    public void register(RegistrationDto registrationDto) {
        SpecialistModel specialist = getSpecialistModel(registrationDto);
        saveToPostgresDataBase(specialist);
        saveToMongoDB(specialist);
        saveToElasticSearchDataBase(specialist);
    }

    private void saveToElasticSearchDataBase(SpecialistModel specialist) {
        ESUtils.mapForES(SpecialistModel.class, specialist).map(specialistSearchRepository::save);
    }

    private void saveToMongoDB(SpecialistModel specialist){
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(specialist.getLogin());
        chatUser.setFirstName(specialist.getFirstName());
        chatUser.setLastName(specialist.getLastName());
        chatUserRepository.save(chatUser);
    }

    private void saveToPostgresDataBase(SpecialistModel specialist) {
        specialistRepository.save(specialist);
    }

    private SpecialistModel getSpecialistModel(RegistrationDto registrationDto) {
        return registrationUtil.create(
                registrationDto,
                Role.ROLE_SPECIALIST,
                SpecialistModel.class
                );
    }
}
