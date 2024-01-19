package rehabilitation.api.service.rehub;


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
import rehabilitation.api.service.business.businessServices.reHubBusiness.ReHubService;
import rehabilitation.api.service.config.ConfigTest;
import rehabilitation.api.service.dto.RehubDto;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestComponent
@Import(ConfigTest.class)
@ExtendWith({MockitoExtension.class})
class ReHubServiceTest {
    private static final String SPECIALIST = "specialist";
    private static final String REHUB = "rehub";

    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private ReHubRepository reHubRepository;
    private ReHubService underTest;
    

    @BeforeEach
    void setUp(){
        underTest = new ReHubService(reHubRepository,specialistRepository);
        reHubRepository.deleteAll();
        specialistRepository.deleteAll();
    }

    @Test
    @DisplayName("tests finding all models and mapping into dto")
    void testGetAllModelView() {
        //given
        List<ReHubModel> rehubs = new ArrayList<>();
        int numberOfInstances = 5;

        for (int i = 0; i < numberOfInstances; i++) {
            ReHubModel reHubModel = createReHub(i);
            rehubs.add(reHubModel);
        }
        Mockito.when(reHubRepository.findAllBy()).thenReturn(rehubs);

        //when
        List<RehubDto> rehubDtos = underTest.getAllModelView();

        //then
        assertDtoListAccordingToTheReHub(rehubDtos, rehubs, numberOfInstances);
        
        verify(reHubRepository, times(1)).findAllBy();
    }

    private void assertDtoListAccordingToTheReHub(List<RehubDto> rehubDtos, List<ReHubModel> reHubs, int numberOfInstances ) {
        for (int i = 0; i<numberOfInstances; i++) {
            RehubDto rehubDto0 = rehubDtos.get(i);
            int actualIndex = i;
            assertThat(rehubDto0).satisfies(
                    rehubDto -> {
                        assertThat(rehubDto.login()).isEqualTo(reHubs.get(actualIndex).getLogin());
                        assertThat(rehubDto.name()).isEqualTo(reHubs.get(actualIndex).getName());
                        assertThat(rehubDto.email()).isEqualTo(reHubs.get(actualIndex).getEmail());
                        assertThat(rehubDto.address()).isEqualTo(reHubs.get(actualIndex).getAddress());
                        assertThat(rehubDto.contactInformation()).isEqualTo(reHubs.get(actualIndex).getContactInformation());
                        assertThat(rehubDto.imgUrl()).isEqualTo(reHubs.get(actualIndex).getImgUrl());
                        assertThat(rehubDto.specialists()).isEqualTo(reHubs.get(actualIndex).getListOfSpecialistsLogin());
                    }
            );
        }
    }

    private void assertDtoAccordingToTheSpecialist(RehubDto rehubDto, ReHubModel reHubModel ) {
            assertThat(rehubDto).satisfies(
                    dto -> {
                        assertThat(rehubDto.login()).isEqualTo(reHubModel.getLogin());
                        assertThat(rehubDto.name()).isEqualTo(reHubModel.getName());
                        assertThat(rehubDto.email()).isEqualTo(reHubModel.getEmail());
                        assertThat(rehubDto.address()).isEqualTo(reHubModel.getAddress());
                        assertThat(rehubDto.contactInformation()).isEqualTo(reHubModel.getContactInformation());
                        assertThat(rehubDto.imgUrl()).isEqualTo(reHubModel.getImgUrl());
                        assertThat(rehubDto.specialists()).isEqualTo(reHubModel.getListOfSpecialistsLogin());
                    }
            );
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




    @Test
    @DisplayName("tests finding model by login and mapping into dto")
    void getClientModelViewByLogin() throws NotFoundLoginException {
        //given
        ReHubModel reHubModel = createReHub(1);
        String rehubLogin = reHubModel.getLogin();

        Mockito.when(reHubRepository.findByLogin(rehubLogin)).thenReturn(Optional.of(reHubModel));

        //when
        RehubDto rehubDto = underTest.getModelDtoByLogin(rehubLogin);

        //then
        assertDtoAccordingToTheSpecialist(rehubDto, reHubModel);
        assertNotFoundLoginException();
    }

    @Test
    @DisplayName("tests deleting deleting model")
    void testDeleteModel() throws NotFoundLoginException {
        //given
        ReHubModel reHubModel = createReHub(1);
        String rehubLogin = reHubModel.getLogin();

        //when
        Mockito.when(reHubRepository.findByLogin(rehubLogin)).thenReturn(Optional.of(reHubModel));
        underTest.deleteModel(rehubLogin);

        //then
        verify(reHubRepository, times(1)).findByLogin(rehubLogin);
        verify(reHubRepository, times(1)).delete(reHubModel);
        assertNotFoundLoginException();
    }

    @Test
    @DisplayName("tests adding specialist to specialist")
    void testAddChildAndRemoveChild() throws NotFoundLoginException {
        //given
        SpecialistModel specialistAboutToBeAdded = createSpecialist(1);
        String specialistAboutToBeAddedLogin = specialistAboutToBeAdded.getLogin();

        SpecialistModel specialistAboutToBeRemoved = createSpecialist(2);
        String specialistAboutToBeRemovedLogin = specialistAboutToBeRemoved.getLogin();

        ReHubModel reHubModel = createReHub(1);
        reHubModel.addSpecialist(specialistAboutToBeRemoved);
        String reHubLogin = reHubModel.getLogin();

        //when
        Mockito.when(reHubRepository.findByLogin(reHubLogin)).thenReturn(Optional.of(reHubModel));
        Mockito.when(specialistRepository.findByLogin(specialistAboutToBeAddedLogin)).thenReturn(Optional.of(specialistAboutToBeAdded));
        Mockito.when(specialistRepository.findByLogin(specialistAboutToBeRemovedLogin)).thenReturn(Optional.of(specialistAboutToBeRemoved));

        underTest.addChild(reHubLogin, specialistAboutToBeAddedLogin);
        underTest.removeChild(reHubLogin, specialistAboutToBeRemovedLogin);

        //then
        verify(reHubRepository, times(2)).findByLogin(reHubLogin);
        verify(specialistRepository, times(1)).findByLogin(specialistAboutToBeAddedLogin);
        verify(specialistRepository, times(1)).findByLogin(specialistAboutToBeRemovedLogin);

        assertThat(reHubModel.getSpecialists()).contains(specialistAboutToBeAdded);
        assertThat(reHubModel.getSpecialists()).doesNotContain(specialistAboutToBeRemoved);
        assertNotFoundLoginException();
    }

    @Test
    void updateModel() throws NotFoundLoginException {
        //given
        ReHubModel reHubModel = createReHub(1);
        String rehubLogin = reHubModel.getLogin();

        Map<String, Object> updates = new HashMap<>();
        updates.put("address", "Naukova");
        updates.put("contactInformation", "0997622482");

        //when
        ReHubService reHubServiceSpy = Mockito.spy(underTest);
        doReturn(reHubModel).when(reHubServiceSpy).getModelIfExists(Mockito.eq(rehubLogin), Mockito.any());

        reHubServiceSpy.updateModel(rehubLogin, updates);

        //then
        assertThat(reHubModel).satisfies( specialist -> {
            assertThat(specialist.getAddress()).isEqualTo(updates.get("address"));
            assertThat(specialist.getContactInformation()).isEqualTo(updates.get("contactInformation"));
        });

//        assertThat(ReHubModel.getAddress()).isNull();
//        assertThat(ReHubModel.getContactInformation()).isNull();
//        assertThat(ReHubModel.getImgUrl()).isNull();
//        assertThat(ReHubModel.getType()).isNull();
//        assertThat(ReHubModel.getRate()).isEqualTo(0);
//        assertThat(ReHubModel.getExperience()).isEqualTo(0);
//        assertThat(ReHubModel.getDescription()).isNull();


        assertNotFoundLoginException();
    }

    /**
     * Asserts if exception has been thrown with non-existing login
     **/
    private void assertNotFoundLoginException() {
        Assertions.assertThatThrownBy(() -> underTest.getModelIfExists("falseLogin", reHubRepository))
                .isInstanceOf(NotFoundLoginException.class)
                .hasMessageContaining("falseLogin");

        Assertions.assertThatThrownBy(() -> underTest.getModelIfExists("falseLogin", specialistRepository))
                .isInstanceOf(NotFoundLoginException.class)
                .hasMessageContaining("falseLogin");
    }
}