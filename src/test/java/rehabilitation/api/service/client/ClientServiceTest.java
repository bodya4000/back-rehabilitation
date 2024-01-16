package rehabilitation.api.service.client;


import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import rehabilitation.api.service.business.ClientService;
import rehabilitation.api.service.config.ConfigTest;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;
import java.util.*;

@TestComponent
@Import(ConfigTest.class)
@ExtendWith({MockitoExtension.class})
class ClientServiceTest {

    private static final String CLIENT = "client";
    private static final String SPECIALIST = "specialist";

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    private ClientService underTest;


    @BeforeEach
    void setUp(){
        underTest = new ClientService(clientRepository, specialistRepository);
    }

    @Test
    @DisplayName("tests finding all models and mapping into dto")
    void testGetAllModelView() {
        //given
        List<ClientModel> clients = new ArrayList<>();
        int numberOfInstances = 5;

        for (int i = 0; i < numberOfInstances; i++) {
            ClientModel clientModel = createClient(i);
            clients.add(clientModel);
        }
        Mockito.when(clientRepository.findAllBy()).thenReturn(clients);

        //when
        List<ClientDto> clientDtos = underTest.getAllModelView();

        //then
        assertDtoListAccordingToTheClient(clientDtos, clients, numberOfInstances);

        verify(clientRepository, times(1)).findAllBy();
    }

    private void assertDtoListAccordingToTheClient(List<ClientDto> clientDtos, List<ClientModel> clients, int numberOfInstances ) {
        for (int i = 0; i<numberOfInstances; i++) {
            ClientDto clientDto0 = clientDtos.get(i);
            int actualIndex = i;
            assertThat(clientDto0).satisfies(
                    clientDto -> {
                        assertThat(clientDto.login()).isEqualTo(clients.get(actualIndex).getLogin());
                        assertThat(clientDto.firstName()).isEqualTo(clients.get(actualIndex).getFirstName());
                        assertThat(clientDto.lastName()).isEqualTo(clients.get(actualIndex).getLastName());
                        assertThat(clientDto.email()).isEqualTo(clients.get(actualIndex).getEmail());
                        assertThat(clientDto.address()).isEqualTo(clients.get(actualIndex).getAddress());
                        assertThat(clientDto.contactInformation()).isEqualTo(clients.get(actualIndex).getContactInformation());
                        assertThat(clientDto.imgUrl()).isEqualTo(clients.get(actualIndex).getImgUrl());
                        assertThat(clientDto.specialistLogin()).isEqualTo(clients.get(actualIndex).getListOfSpecialistsLogin());
                    }
            );
        }
    }

    private void assertDtoAccordingToTheClient(ClientDto clientDto, ClientModel client ) {
            assertThat(clientDto).satisfies(
                    dto -> {
                        assertThat(dto.login()).isEqualTo(client.getLogin());
                        assertThat(dto.firstName()).isEqualTo(client.getFirstName());
                        assertThat(dto.lastName()).isEqualTo(client.getLastName());
                        assertThat(dto.email()).isEqualTo(client.getEmail());
                        assertThat(dto.address()).isEqualTo(client.getAddress());
                        assertThat(dto.contactInformation()).isEqualTo(client.getContactInformation());
                        assertThat(dto.imgUrl()).isEqualTo(client.getImgUrl());
                        assertThat(dto.specialistLogin()).isEqualTo(client.getListOfSpecialistsLogin());
                    }
            );
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

    @Test
    @DisplayName("tests finding model by login and mapping into dto")
    void getClientModelViewByLogin() throws NotFoundLoginException {
        //given
        ClientModel clientModel = createClient(1);
        String clientLogin = clientModel.getLogin();

        Mockito.when(clientRepository.findByLogin(clientLogin)).thenReturn(Optional.of(clientModel));

        //when
        ClientDto clientDto = underTest.getModelViewByLogin(clientLogin);

        //then
        assertDtoAccordingToTheClient(clientDto, clientModel);
        assertNotFoundLoginException();
    }

    @Test
    @DisplayName("tests deleting deleting model")
    void testDeleteModel() throws NotFoundLoginException {
        //given
        ClientModel clientModel = createClient(1);
        String clientLogin = clientModel.getLogin();

        //when
        Mockito.when(clientRepository.findByLogin(clientLogin)).thenReturn(Optional.of(clientModel));
        underTest.deleteModel(clientLogin);

        //then
        verify(clientRepository, times(1)).findByLogin(clientLogin);
        verify(clientRepository, times(1)).delete(clientModel);
        assertNotFoundLoginException();
    }

    @Test
    @DisplayName("tests adding specialist to client")
    void testAddChildAndRemoveChild() throws NotFoundLoginException {
        //given
        SpecialistModel specialistAboutToBeAdded = createSpecialist(1);
        String specialistAboutToBeAddedLogin = specialistAboutToBeAdded.getLogin();

        SpecialistModel specialistAboutToBeRemoved = createSpecialist(2);
        String specialistAboutToBeRemovedLogin = specialistAboutToBeRemoved.getLogin();

        ClientModel clientModel = createClient(1);
        clientModel.addSpecialist(specialistAboutToBeRemoved);
        String clientLogin = clientModel.getLogin();

        //when
        Mockito.when(clientRepository.findByLogin(clientLogin)).thenReturn(Optional.of(clientModel));
        Mockito.when(specialistRepository.findByLogin(specialistAboutToBeAddedLogin)).thenReturn(Optional.of(specialistAboutToBeAdded));
        Mockito.when(specialistRepository.findByLogin(specialistAboutToBeRemovedLogin)).thenReturn(Optional.of(specialistAboutToBeRemoved));

        underTest.addChild(clientLogin, specialistAboutToBeAddedLogin);
        underTest.removeChild(clientLogin, specialistAboutToBeRemovedLogin);

        //then
        verify(clientRepository, times(2)).findByLogin(clientLogin);
        verify(specialistRepository, times(1)).findByLogin(specialistAboutToBeAddedLogin);
        verify(specialistRepository, times(1)).findByLogin(specialistAboutToBeRemovedLogin);

        assertThat(clientModel.getSpecialists()).contains(specialistAboutToBeAdded);
        assertThat(clientModel.getSpecialists()).doesNotContain(specialistAboutToBeRemoved);
        assertNotFoundLoginException();
    }

    @Test
    void loadModel() throws NotFoundLoginException {
        //given
        ClientModel clientModel = createClient(1);
        String clientLogin = clientModel.getLogin();

        //when
        Mockito.when(clientRepository.findByLogin(clientLogin)).thenReturn(Optional.of(clientModel));
        ClientModel clientModel1 = underTest.loadModel(clientLogin);

        //then
        verify(clientRepository, times(1)).findByLogin(clientLogin);
        assertThat(clientModel).isEqualTo(clientModel1);
        assertNotFoundLoginException();
    }


    @Test
    void updateModel() throws NotFoundLoginException {
        //given
        ClientModel clientModel = createClient(1);
        String clientLogin = clientModel.getLogin();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "John");
        updates.put("lastName", "Doe");
        updates.put("email", "john.doe@example.com");

        //when
        ClientService clientServiceSpy = Mockito.spy(underTest);
        doReturn(clientModel).when(clientServiceSpy).getModelIfExists(Mockito.eq(clientLogin), Mockito.any());

        clientServiceSpy.updateModel(clientLogin, updates);

        //then
        assertThat(clientModel).satisfies( client -> {
            assertThat(client.getFirstName()).isEqualTo(updates.get("firstName"));
            assertThat(client.getLastName()).isEqualTo(updates.get("lastName"));
            assertThat(client.getEmail()).isEqualTo(updates.get("email"));
        });

        assertThat(clientModel.getAddress()).isNull();
        assertThat(clientModel.getContactInformation()).isNull();
        assertThat(clientModel.getImgUrl()).isNull();

        assertNotFoundLoginException();
    }

    /**
     * Asserts if exception has been thrown with non-existing login
     **/
    private void assertNotFoundLoginException() {
        Assertions.assertThatThrownBy(() -> underTest.getModelIfExists("falseLogin", clientRepository))
                .isInstanceOf(NotFoundLoginException.class)
                .hasMessageContaining("falseLogin");

        Assertions.assertThatThrownBy(() -> underTest.getModelIfExists("falseLogin", specialistRepository))
                .isInstanceOf(NotFoundLoginException.class)
                .hasMessageContaining("falseLogin");
    }
}