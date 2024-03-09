package rehabilitation.api.service.services.business.registration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.AdminRegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.RegistrationUtil;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.AdminModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.AdminRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationAdminServiceTest {

    @InjectMocks
    private AdminRegistrationService underTheTest;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private ChatUserRepository chatUserRepository;
    @Mock
    private RegistrationUtil registrationUtil;


    @Test
    void Should_SuccessfullyRegisterAdminAndSaveToPostgresAndMongoDB() {
        // given
        var registration = getRegistration();

        // when
        var adminPostgresModel = createPostgresAdmin(registration);
        var adminMongoModel = createMongoAdmin(registration);
        when(registrationUtil.create(registration, Role.ROLE_ADMIN, AdminModel.class)).thenReturn(
                adminPostgresModel
        );
        underTheTest.register(registration);

        // then
        verify(adminRepository, times(1)).save(adminPostgresModel);
        verify(chatUserRepository, times(1)).save(adminMongoModel);
    }


    private RegistrationDto getRegistration() {
        return new RegistrationDto("login", "email", "password", UserType.CLIENT);
    }

    public AdminModel createPostgresAdmin(RegistrationDto registrationDto) {
        AdminModel adminModel = new AdminModel();
        adminModel.setLogin(registrationDto.login());
        adminModel.setEmail(registrationDto.email());
        var userRole = new UserRole(Role.ROLE_ADMIN, adminModel);
        adminModel.getRoles().add(userRole);
        return adminModel;
    }

    public ChatUser createMongoAdmin(RegistrationDto registrationDto) {
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(registrationDto.login());
        return chatUser;
    }
}
