package rehabilitation.api.service.services.business.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import rehabilitation.api.service.business.businessServices.securityBusiness.ValidateService;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.repositories.jpa.UserRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@ExtendWith(MockitoExtension.class)
public class ValidateServiceTest {
    @InjectMocks
    private ValidateService underTheTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatUserRepository chatUserRepository;

    // positive

    @Test
    void Should_NotThrowAlreadyExistLoginException_When_RegistrationDtoCorrect() throws AlreadyExistLoginException {
        // given
        var registration = new RegistrationDto("login", "email", "password", UserType.ADMIN);

        when(userRepository.existsByLogin(registration.login())).thenReturn(false);
        when(chatUserRepository.existsChatUserByLogin(registration.login())).thenReturn(false);
        when(userRepository.existsByEmail(registration.email())).thenReturn(false);

        // when
        underTheTest.validateRegistration(registration);
    }

    // negative

    @Test
    void Should_ThrowAlreadyExistLoginException_When_LoginExistsInPostgres() {
        // given
        var registration = new RegistrationDto("existingLogin", "email", "password", UserType.ADMIN);

        when(userRepository.existsByLogin(registration.login())).thenReturn(true);

        // when/assert
        Assertions.assertThatThrownBy(() -> underTheTest.validateRegistration(registration))
                .isInstanceOf(AlreadyExistLoginException.class)
                .hasMessageContaining(registration.login());
    }

    @Test
    void Should_ThrowAlreadyExistLoginException_When_LoginExistsInMongo() {
        // given
        var registration = new RegistrationDto("existingLogin", "email", "password", UserType.ADMIN);

        when(chatUserRepository.existsChatUserByLogin(registration.login())).thenReturn(true);

        // when/assert
        Assertions.assertThatThrownBy(() -> underTheTest.validateRegistration(registration))
                .isInstanceOf(AlreadyExistLoginException.class)
                .hasMessageContaining(registration.login());
    }


    @Test
    void Should_ThrowAlreadyExistLoginException_When_EmailExists() {
        // given
        var registration = new RegistrationDto("login", "existingEmail", "password", UserType.ADMIN);

        when(userRepository.existsByLogin(registration.login())).thenReturn(false);
        when(chatUserRepository.existsChatUserByLogin(registration.login())).thenReturn(false);
        when(userRepository.existsByEmail(registration.email())).thenReturn(true);

        // when/assert
        Assertions.assertThatThrownBy(() -> underTheTest.validateRegistration(registration))
                .isInstanceOf(AlreadyExistLoginException.class)
                .hasMessageContaining(registration.email());
    }

}
