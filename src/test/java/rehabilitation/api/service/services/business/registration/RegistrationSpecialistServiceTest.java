package rehabilitation.api.service.services.business.registration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.RegistrationUtil;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.SpecialistRegistrationService;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.elasticsearch.SpecialistSearchRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestComponent
public class RegistrationSpecialistServiceTest {

    @InjectMocks
    private SpecialistRegistrationService underTheTest;
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private ChatUserRepository chatUserRepository;
    @Mock
    private SpecialistSearchRepository specialistSearchRepository;
    @Mock
    private RegistrationUtil registrationUtil;

    @Test
    void Should_SuccessfullyRegisterSpecialistAndSaveToPostgresAndMongoDBAndElasticSearch() {
        // given
        var registration = getRegistration();

        // when
        var specialistPostgresModel = createPostgresSpecialist(registration);
        var specialistMongoModel = createMongoSpecialist(registration);
        when(registrationUtil.create(registration, Role.ROLE_SPECIALIST, SpecialistModel.class)).thenReturn(
                specialistPostgresModel
        );
        underTheTest.register(registration);

        // then
        verify(specialistRepository, times(1)).save(specialistPostgresModel);
        verify(specialistSearchRepository, times(1)).save(specialistPostgresModel);
        verify(chatUserRepository, times(1)).save(specialistMongoModel);
    }


    private RegistrationDto getRegistration() {
        return new RegistrationDto("login", "email", "password", UserType.CLIENT);
    }

    public SpecialistModel createPostgresSpecialist(RegistrationDto registrationDto) {
        SpecialistModel specialistModel = new SpecialistModel();
        specialistModel.setLogin(registrationDto.login());
        specialistModel.setEmail(registrationDto.email());
        var userRole = new UserRole(Role.ROLE_SPECIALIST, specialistModel);
        specialistModel.getRoles().add(userRole);
        return specialistModel;
    }

    public ChatUser createMongoSpecialist(RegistrationDto registrationDto) {
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(registrationDto.login());
        return chatUser;
    }
}
