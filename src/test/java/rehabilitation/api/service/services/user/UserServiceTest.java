package rehabilitation.api.service.services.user;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import rehabilitation.api.service.business.businessServices.userBusiness.UserService;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.*;
import rehabilitation.api.service.utills.EntityType;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@TestComponent
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService underTheTest;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ReHubRepository reHubRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void Should_LoadUserByUserName_InJpaAllRepositories() {
        testUserByUsername(UserModel.class, userRepository, times(1));
        testUserByUsername(ClientModel.class, clientRepository, times(2));
        testUserByUsername(SpecialistModel.class, specialistRepository, times(3));
        testUserByUsername(ReHubModel.class, reHubRepository, times(4));
    }

    private <T extends UserModel> void testUserByUsername(Class<T> userType, CommonRepository<T> repository, VerificationMode verificationMode) {
        // given
        var user = generateUser(1, userType);
        when(repository.findByLoginFetchRoles(user.getLogin())).thenReturn((Optional<T>) Optional.of(user));

        // when
        UserDetails expected = underTheTest.loadUserByUsername(user.getLogin());

        // then
        assertUserDetailsAreCorrect(expected, user);

        verify(repository, verificationMode).findByLoginFetchRoles(user.getLogin());
    }
    private static void assertUserDetailsAreCorrect(UserDetails expected, UserModel user) {
        assertThat(expected).satisfies(
                userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(user.getLogin());
                    assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
                    assertThat(userDetails.getAuthorities()).isEqualTo(user.getRoles());
                }
        );
    }

    private UserModel generateUser(int index, Class<? extends UserModel> userType) {
        try {
            UserModel user = userType.getDeclaredConstructor().newInstance();
            user.setLogin(EntityType.CLIENT.name() + index);
            user.setPassword(EntityType.CLIENT.name());
            user.setEmail(user.getLogin() + "@mail.com");
            user.getRoles().add(new UserRole(Role.ROLE_CLIENT, user));
            return user;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error generating user", e);
        }
    }

}
