package rehabilitation.api.service.business.businessServices.securityBusiness.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.AdminModel;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.repositories.jpa.AdminRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class AdminRegistrationService implements Registration{

    private final AdminRepository adminRepository;
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationUtil registrationUtil;

    @Override
    public void register(RegistrationDto registrationDto) {
        var admin = createAdmin(registrationDto);
        saveToPostgresDataBase(admin);
        saveToMongoDB(admin);
    }

    private AdminModel createAdmin(RegistrationDto registrationDto) {
        return registrationUtil.create(registrationDto, Role.ROLE_ADMIN, AdminModel.class);
    }

    private void saveToPostgresDataBase(AdminModel admin) {
        adminRepository.save(admin);
    }
    private void saveToMongoDB(AdminModel client){
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(client.getLogin());
        chatUserRepository.save(chatUser);
    }

}
