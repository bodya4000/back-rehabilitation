package rehabilitation.api.service.business.businessServices.securityBusiness.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class ReHubRegistrationService implements Registration{
    private final ReHubRepository reHubRepository;
    private final RegistrationUtil registrationUtil;
    private final ChatUserRepository chatUserRepository;

    @Override
    public void register(RegistrationDto registrationDto) {
        ReHubModel reHubModel = createReHub(registrationDto);
        saveToPostgresDataBase(reHubModel);
        saveToMongoDB(reHubModel);
    }

    private void saveToPostgresDataBase(ReHubModel reHubModel) {
        reHubRepository.save(reHubModel);
    }

    private void saveToMongoDB(ReHubModel reHubModel){
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(reHubModel.getLogin());
        chatUserRepository.save(chatUser);
    }

    private ReHubModel createReHub(RegistrationDto registrationDto) {
        return registrationUtil.create(
                registrationDto,
                Role.ROLE_REHUB,
                ReHubModel.class);
    }


}
