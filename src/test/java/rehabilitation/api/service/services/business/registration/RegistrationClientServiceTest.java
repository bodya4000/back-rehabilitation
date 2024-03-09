package rehabilitation.api.service.services.business.registration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.ClientRegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.RegistrationUtil;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@ExtendWith(MockitoExtension.class)
public class RegistrationClientServiceTest {

    @InjectMocks
    private ClientRegistrationService underTheTest;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ChatUserRepository chatUserRepository;
    @Mock
    private RegistrationUtil registrationUtil;


    @Test
    void Should_SuccessfullyRegisterClientAndSaveToPostgresAndMongoDB() {
        // given
        var registration = getRegistration();

        // when
        var clientPostgresModel = createPostgresClient(registration);
        var clientMongoModel = createMongoClient(registration);
        when(registrationUtil.create(registration, Role.ROLE_CLIENT, ClientModel.class)).thenReturn(
                clientPostgresModel
        );
        underTheTest.register(registration);

        // then
        verify(clientRepository, times(1)).save(clientPostgresModel);
        verify(chatUserRepository, times(1)).save(clientMongoModel);
    }


    private RegistrationDto getRegistration() {
        return new RegistrationDto("login", "email", "password", UserType.CLIENT);
    }

    public ClientModel createPostgresClient(RegistrationDto registrationDto) {
        ClientModel clientModel = new ClientModel();
        clientModel.setLogin(registrationDto.login());
        clientModel.setEmail(registrationDto.email());
        var userRole = new UserRole(Role.ROLE_CLIENT, clientModel);
        clientModel.getRoles().add(userRole);
        return clientModel;
    }

    public ChatUser createMongoClient(RegistrationDto registrationDto) {
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(registrationDto.login());
        return chatUser;
    }
}
