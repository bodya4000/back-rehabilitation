package rehabilitation.api.service.specialist;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.parameters.P;
import rehabilitation.api.service.business.SpecialistService;
import rehabilitation.api.service.config.ConfigTest;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestComponent
@Import(ConfigTest.class)
@ExtendWith({MockitoExtension.class})
class SpecialistServiceTest {

    private static final String CLIENT = "client";
    private static final String SPECIALIST = "specialist";

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    private SpecialistService underTest;
    

    @BeforeEach
    void setUp(){
        underTest = new SpecialistService(specialistRepository, clientRepository);
    }

    @Test
    @DisplayName("tests finding all models and mapping into dto")
    void testGetAllModelView() {
        //given
        List<SpecialistModel> specialists = new ArrayList<>();
        int numberOfInstances = 5;

        for (int i = 0; i < numberOfInstances; i++) {
            SpecialistModel specialistModel = createSpecialist(i);
            specialists.add(specialistModel);
        }
        Mockito.when(specialistRepository.findAllBy()).thenReturn(specialists);

        //when
        List<SpecialistDto> specialistDtos = underTest.getAllModelView();

        //then
        assertDtoListAccordingToTheClient(specialistDtos, specialists, numberOfInstances);
        
        verify(specialistRepository, times(1)).findAllBy();
    }

    private void assertDtoListAccordingToTheClient(List<SpecialistDto> specialistDtos, List<SpecialistModel> specialists, int numberOfInstances ) {
        for (int i = 0; i<numberOfInstances; i++) {
            SpecialistDto specialistDto0 = specialistDtos.get(i);
            int actualIndex = i;
            assertThat(specialistDto0).satisfies(
                    specialistDto -> {
                        assertThat(specialistDto.login()).isEqualTo(specialists.get(actualIndex).getLogin());
                        assertThat(specialistDto.firstName()).isEqualTo(specialists.get(actualIndex).getFirstName());
                        assertThat(specialistDto.lastName()).isEqualTo(specialists.get(actualIndex).getLastName());
                        assertThat(specialistDto.city()).isEqualTo(specialists.get(actualIndex).getCity());
                        assertThat(specialistDto.age()).isEqualTo(specialists.get(actualIndex).getAge());
                        assertThat(specialistDto.experience()).isEqualTo(specialists.get(actualIndex).getExperience());
                        assertThat(specialistDto.rate()).isEqualTo(specialists.get(actualIndex).getRate());
                        assertThat(specialistDto.type()).isEqualTo(specialists.get(actualIndex).getType());
                        assertThat(specialistDto.imgUrl()).isEqualTo(specialists.get(actualIndex).getImgUrl());
                        assertThat(specialistDto.description()).isEqualTo(specialists.get(actualIndex).getDescription());
                        if(specialists.get(actualIndex).getReHub() == null) {
                            assertThat(specialistDto.rehub()).isEmpty();
                        } else {
                            assertThat(specialistDto.rehub()).isEqualTo(specialists.get(actualIndex).getReHub().getLogin());
                        }
                        assertThat(specialistDto.clientLogin()).isEqualTo(specialists.get(actualIndex).getListOfClientsLogin());
                    }
            );
        }
    }

    private void assertDtoAccordingToTheSpecialist(SpecialistDto specialistDto, SpecialistModel specialist ) {
            assertThat(specialistDto).satisfies(
                    dto -> {
                        assertThat(specialistDto.login()).isEqualTo(specialist.getLogin());
                        assertThat(specialistDto.firstName()).isEqualTo(specialist.getFirstName());
                        assertThat(specialistDto.lastName()).isEqualTo(specialist.getLastName());
                        assertThat(specialistDto.city()).isEqualTo(specialist.getCity());
                        assertThat(specialistDto.age()).isEqualTo(specialist.getAge());
                        assertThat(specialistDto.experience()).isEqualTo(specialist.getExperience());
                        assertThat(specialistDto.rate()).isEqualTo(specialist.getRate());
                        assertThat(specialistDto.type()).isEqualTo(specialist.getType());
                        assertThat(specialistDto.imgUrl()).isEqualTo(specialist.getImgUrl());
                        assertThat(specialistDto.description()).isEqualTo(specialist.getDescription());
                        if(specialist.getReHub() == null) {
                            assertThat(specialistDto.rehub()).isEmpty();
                        } else {
                            assertThat(specialistDto.rehub()).isEqualTo(specialist.getReHub().getLogin());
                        }
                        assertThat(specialistDto.clientLogin()).isEqualTo(specialist.getListOfClientsLogin());
                    }
            );
    }

    private ClientModel createClient(int index) {
        ClientModel clientModel  = new ClientModel();
        clientModel.setLogin(CLIENT + index);
        clientModel.setPassword("specialist");
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
        SpecialistModel specialistModel = createSpecialist(1);
        String specialistLogin = specialistModel.getLogin();

        Mockito.when(specialistRepository.findByLogin(specialistLogin)).thenReturn(Optional.of(specialistModel));

        //when
        SpecialistDto specialistDto = underTest.getModelViewByLogin(specialistLogin);

        //then
        assertDtoAccordingToTheSpecialist(specialistDto, specialistModel);
        assertNotFoundLoginException();
    }

    @Test
    @DisplayName("tests deleting deleting model")
    void testDeleteModel() throws NotFoundLoginException {
        //given
        SpecialistModel specialistModel = createSpecialist(1);
        String specialistLogin = specialistModel.getLogin();

        //when
        Mockito.when(specialistRepository.findByLogin(specialistLogin)).thenReturn(Optional.of(specialistModel));
        underTest.deleteModel(specialistLogin);

        //then
        verify(specialistRepository, times(1)).findByLogin(specialistLogin);
        verify(specialistRepository, times(1)).delete(specialistModel);
        assertNotFoundLoginException();
    }

    @Test
    @DisplayName("tests adding specialist to specialist")
    void testAddChildAndRemoveChild() throws NotFoundLoginException {
        //given
        ClientModel clientAboutToBeAdded = createClient(1);
        String clientAboutToBeAddedLogin = clientAboutToBeAdded.getLogin();

        ClientModel clientAboutToBeRemoved = createClient(2);
        String clientAboutToBeRemovedLogin = clientAboutToBeRemoved.getLogin();

        SpecialistModel specialistModel = createSpecialist(1);
        specialistModel.addClient(clientAboutToBeRemoved);
        String specialistLogin = specialistModel.getLogin();

        //when
        Mockito.when(specialistRepository.findByLogin(specialistLogin)).thenReturn(Optional.of(specialistModel));
        Mockito.when(clientRepository.findByLogin(clientAboutToBeAddedLogin)).thenReturn(Optional.of(clientAboutToBeAdded));
        Mockito.when(clientRepository.findByLogin(clientAboutToBeRemovedLogin)).thenReturn(Optional.of(clientAboutToBeRemoved));

        underTest.addChild(specialistLogin, clientAboutToBeAddedLogin);
        underTest.removeChild(specialistLogin, clientAboutToBeRemovedLogin);

        //then
        verify(specialistRepository, times(2)).findByLogin(specialistLogin);
        verify(clientRepository, times(1)).findByLogin(clientAboutToBeAddedLogin);
        verify(clientRepository, times(1)).findByLogin(clientAboutToBeRemovedLogin);

        assertThat(specialistModel.getClients()).contains(clientAboutToBeAdded);
        assertThat(specialistModel.getClients()).doesNotContain(clientAboutToBeRemoved);
        assertNotFoundLoginException();
    }
//
//    @Test
//    void loadModel() throws NotFoundLoginException {
//        //given
//        SpecialistModel specialistModel = createSpecialist(1);
//        String specialistLogin = specialistModel.getLogin();
//
//        //when
//        Mockito.when(specialistRepository.findByLogin(specialistLogin)).thenReturn(Optional.of(specialistModel));
//        SpecialistModel specialistModel1 = underTest.loadModel(specialistLogin);
//
//        //then
//        verify(specialistRepository, times(1)).findByLogin(specialistLogin);
//        assertThat(specialistModel).isEqualTo(specialistModel1);
//        assertNotFoundLoginException();
//    }


    @Test
    void updateModel() throws NotFoundLoginException {
        //given
        SpecialistModel specialistModel = createSpecialist(1);
        String specialistLogin = specialistModel.getLogin();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "John");
        updates.put("lastName", "Doe");

        //when
        SpecialistService specialistServiceSpy = Mockito.spy(underTest);
        doReturn(specialistModel).when(specialistServiceSpy).getModelIfExists(Mockito.eq(specialistLogin), Mockito.any());

        specialistServiceSpy.updateModel(specialistLogin, updates);

        //then
        assertThat(specialistModel).satisfies( specialist -> {
            assertThat(specialist.getFirstName()).isEqualTo(updates.get("firstName"));
            assertThat(specialist.getLastName()).isEqualTo(updates.get("lastName"));
        });

        assertThat(specialistModel.getAddress()).isNull();
        assertThat(specialistModel.getContactInformation()).isNull();
        assertThat(specialistModel.getImgUrl()).isNull();
        assertThat(specialistModel.getType()).isNull();
        assertThat(specialistModel.getRate()).isEqualTo(0);
        assertThat(specialistModel.getExperience()).isEqualTo(0);
        assertThat(specialistModel.getDescription()).isNull();


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