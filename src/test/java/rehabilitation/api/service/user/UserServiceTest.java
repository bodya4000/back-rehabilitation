package rehabilitation.api.service.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import rehabilitation.api.service.business.businessServices.userBusiness.UserService;
import rehabilitation.api.service.config.ConfigTest;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(ConfigTest.class)
class UserServiceTest {
    private static final String SPECIALIST = "specialist";
    private static final String REHUB = "rehub";
    private static final String CLIENT = "client";
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ReHubRepository reHubRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new UserService(clientRepository, reHubRepository, specialistRepository);
    }


    @Test
    void loadUserByUsername() {
        //given
        ClientModel clientModel = createClient(1);
        SpecialistModel specialistModel = createSpecialist(1);
        ReHubModel reHubModel = createReHub(1);

        String clientLogin = clientModel.getLogin();
        String specialistLogin = specialistModel.getLogin();
        String reHubLogin = reHubModel.getLogin();

        //when
        when(clientRepository.findByLogin(anyString())).thenAnswer(invocation -> {
            if (clientLogin.equals(invocation.getArgument(0))) {
                return Optional.of(clientModel);
            }
            return Optional.empty();
        });

        when(specialistRepository.findByLogin(anyString())).thenAnswer(invocation -> {
            if (specialistLogin.equals(invocation.getArgument(0))) {
                return Optional.of(specialistModel);
            }
            return Optional.empty();
        });

        when(reHubRepository.findByLogin(anyString())).thenAnswer(invocation -> {
            if (reHubLogin.equals(invocation.getArgument(0))) {
                return Optional.of(reHubModel);
            }
            return Optional.empty();
        });


        UserDetails clientDetails = underTest.loadUserByUsername(clientLogin);
        UserDetails specialistDetails = underTest.loadUserByUsername(specialistLogin);
        UserDetails reHubDetails = underTest.loadUserByUsername(reHubLogin);

        //then
        verify(clientRepository, times(1)).findByLogin(clientLogin);
        verify(specialistRepository, times(1)).findByLogin(specialistLogin);
        verify(reHubRepository, times(1)).findByLogin(reHubLogin);

        assertUserDetailsToModel(clientDetails, clientModel);
        assertUserDetailsToModel(specialistDetails, specialistModel);
        assertUserDetailsToModel(reHubDetails, reHubModel);

        assertNotFoundLoginException();

    }

    private void assertUserDetailsToModel(UserDetails modelDetails, UserModel model) {
        assertThat(modelDetails).satisfies(userDetails -> {
            assertThat(userDetails.getUsername()).isEqualTo(model.getLogin());
            assertThat(userDetails.getPassword()).isEqualTo(model.getPassword());
            assertThat(userDetails.getAuthorities()).isEqualTo(model.getRoles());
        });
    }

    private ClientModel createClient(int index) {
        ClientModel clientModel = new ClientModel();
        clientModel.setLogin(CLIENT + index);
        clientModel.setPassword("client");
        clientModel.setEmail(clientModel.getLogin() + "@mail.com");
        clientModel.getRoles().add(new UserRole(Role.ROLE_CLIENT, clientModel));

        lenient().when(clientRepository.save(clientModel)).thenReturn(clientModel);
        return clientModel;
    }

    private SpecialistModel createSpecialist(int index) {
        SpecialistModel specialistModel = new SpecialistModel();
        specialistModel.setLogin(SPECIALIST + index);
        specialistModel.setPassword("specialist");
        specialistModel.setEmail(specialistModel.getLogin() + "@mail.com");
        specialistModel.getRoles().add(new UserRole(Role.ROLE_SPECIALIST, specialistModel));

        lenient().when(specialistRepository.save(specialistModel)).thenReturn(specialistModel);

        return specialistModel;
    }

    private ReHubModel createReHub(int index) {
        ReHubModel reHubModel = new ReHubModel();
        reHubModel.setLogin(REHUB + index);
        reHubModel.setPassword("rehub");
        reHubModel.setEmail(reHubModel.getLogin() + "@mail.com");
        reHubModel.getRoles().add(new UserRole(Role.ROLE_REHUB, reHubModel));

        lenient().when(reHubRepository.save(reHubModel)).thenReturn(reHubModel);

        return reHubModel;
    }

    /**
     * Asserts if exception has been thrown with non-existing login
     **/
    private void assertNotFoundLoginException() {
        assertThatThrownBy(() -> underTest.loadUserByUsername("falseLogin"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("falseLogin");

    }

}