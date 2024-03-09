package rehabilitation.api.service.services.business.registration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.ReHubRegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.RegistrationUtil;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestComponent
public class RegistrationReHubServiceTest {

    @InjectMocks
    private ReHubRegistrationService underTheTest;
    @Mock
    private ReHubRepository reHubRepository;
    @Mock
    private ChatUserRepository chatUserRepository;
    @Mock
    private RegistrationUtil registrationUtil;

    @Test
    void Should_SuccessfullyRegisterReHubAndSaveToPostgresAndMongoDBAndElasticSearch() {
        // given
        var registration = getRegistration();

        // when
        var reHubPostgresModel = createPostgresReHub(registration);
        var reHubMongoModel = createMongoReHub(registration);
        when(registrationUtil.create(registration, Role.ROLE_REHUB, ReHubModel.class)).thenReturn(
                reHubPostgresModel
        );
        underTheTest.register(registration);

        // then
        verify(reHubRepository, times(1)).save(reHubPostgresModel);
        verify(chatUserRepository, times(1)).save(reHubMongoModel);
    }


    private RegistrationDto getRegistration() {
        return new RegistrationDto("login", "email", "password", UserType.CLIENT);
    }

    public ReHubModel createPostgresReHub(RegistrationDto registrationDto) {
        ReHubModel reHubModel = new ReHubModel();
        reHubModel.setLogin(registrationDto.login());
        reHubModel.setEmail(registrationDto.email());
        var userRole = new UserRole(Role.ROLE_REHUB, reHubModel);
        reHubModel.getRoles().add(userRole);
        return reHubModel;
    }

    public ChatUser createMongoReHub(RegistrationDto registrationDto) {
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(registrationDto.login());
        return chatUser;
    }
}
