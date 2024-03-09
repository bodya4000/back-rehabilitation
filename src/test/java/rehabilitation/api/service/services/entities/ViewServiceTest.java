package rehabilitation.api.service.services.entities;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import rehabilitation.api.service.business.businessServices.clientBusiness.view.ClientViewService;
import rehabilitation.api.service.business.businessServices.reHubBusiness.view.ReHubViewService;
import rehabilitation.api.service.business.businessServices.specialistBusiness.view.SpecialistViewService;
import rehabilitation.api.service.business.businessUtils.MappingUtil;
import rehabilitation.api.service.dto.entities.ClientDto;
import rehabilitation.api.service.dto.entities.RehubDto;
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class ViewServiceTest {
    private ReHubViewService reHubViewService;
    private SpecialistViewService specialistViewService;
    private ClientViewService clientViewService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private ReHubRepository reHubRepository;
    @Mock
    private MappingUtil mappingUtil;

    @BeforeEach
    public void setUp() {
        clientViewService = new ClientViewService(clientRepository, mappingUtil);
        specialistViewService = new SpecialistViewService(specialistRepository, mappingUtil);
        reHubViewService = new ReHubViewService(reHubRepository, mappingUtil);
    }

    // positive

    @Test
    void Should_LoadClientDto_When_CorrectLogin() throws NotFoundLoginException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Arrange
        ClientModel clientModel = (ClientModel) createModel(1, ClientModel.class);
        String login = clientModel.getLogin();
        var expected = new ClientDto(
                login, null, null, null, null, null, null,
                null);

        when(clientRepository.findByLogin(login)).thenReturn(java.util.Optional.of(clientModel));
        when(mappingUtil.doMapClientDtoAndGet(clientModel)).thenReturn(expected);

        // Act
        ClientDto result = clientViewService.getModelDtoByLogin(login);

        // Assert

        assertThat(result).isEqualTo(expected);

        verify(clientRepository, times(1)).findByLogin(login);
        verify(mappingUtil, times(1)).doMapClientDtoAndGet(clientModel);
    }

    @Test
    void Should_LoadListOfClientDto() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Arrange
        List<ClientDto> expectedListOfDto = new ArrayList<>();
        List<ClientModel> expectedListOfModel = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientModel clientModel = (ClientModel) createModel(1, ClientModel.class);
            expectedListOfModel.add(clientModel);
            expectedListOfDto.add(new ClientDto(
                    clientModel.getLogin(), null, null, null, null, null, null,
                    null));
        }

        when(clientRepository.findAllBy()).thenReturn(expectedListOfModel);
        expectedListOfModel.forEach(
                model -> when(mappingUtil.doMapClientDtoAndGet(model)).thenReturn(
                        expectedListOfDto.stream().filter(clientDto -> clientDto.login().equals(model.getLogin()))
                                .findFirst().get()));

        // Act
        List<ClientDto> result = clientViewService.getListOfModelDto();

        // Assert
        assertThat(result).isEqualTo(expectedListOfDto);

        verify(clientRepository, times(1)).findAllBy();
        verify(mappingUtil, times(expectedListOfDto.size())).doMapClientDtoAndGet(any(ClientModel.class));
    }

    @Test
    void Should_LoadListOfSpecialistDto() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Arrange
        List<SpecialistDto> expectedListOfDto = new ArrayList<>();
        List<SpecialistModel> expectedListOfModel = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SpecialistModel specialistModel = (SpecialistModel) createModel(1, SpecialistModel.class);
            expectedListOfModel.add(specialistModel);
            expectedListOfDto.add(new SpecialistDto(
                    specialistModel.getLogin(), null, null, null, 0, 0, 0,
                    null, null, null, null, null));
        }

        when(specialistRepository.findAllBy()).thenReturn(expectedListOfModel);
        expectedListOfModel.forEach(
                model -> when(mappingUtil.doMapSpecialistDtoAndGet(model)).thenReturn(
                        expectedListOfDto.stream().filter(clientDto -> clientDto.login().equals(model.getLogin()))
                                .findFirst().get()));

        // Act
        List<SpecialistDto> result = specialistViewService.getListOfModelDto();

        // Assert
        assertThat(result).isEqualTo(expectedListOfDto);

        verify(specialistRepository, times(1)).findAllBy();
        verify(mappingUtil, times(expectedListOfDto.size())).doMapSpecialistDtoAndGet(any(SpecialistModel.class));
    }

    @Test
    void Should_LoadSpecialistDto_When_CorrectLogin() throws NotFoundLoginException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Arrange
        SpecialistModel specialistModel = (SpecialistModel) createModel(1, SpecialistModel.class);
        String login = specialistModel.getLogin();
        var expected = new SpecialistDto(
                login, null, null, null, 0, 0, 0,
                null, null, null, null, null);

        when(specialistRepository.findByLogin(login)).thenReturn(java.util.Optional.of(specialistModel));
        when(mappingUtil.doMapSpecialistDtoAndGet(specialistModel)).thenReturn(expected);

        // Act
        SpecialistDto result = specialistViewService.getModelDtoByLogin(login);

        // Assert

        assertThat(result).isEqualTo(expected);

        verify(specialistRepository, times(1)).findByLogin(login);
        verify(mappingUtil, times(1)).doMapSpecialistDtoAndGet(specialistModel);
    }

    @Test
    void Should_LoadReHubDto_When_CorrectLogin() throws NotFoundLoginException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Arrange
        ReHubModel reHubModel = (ReHubModel) createModel(1, ReHubModel.class);
        String login = reHubModel.getLogin();
        var expected = new RehubDto(
                login, null, null, null, null, null, null);

        when(reHubRepository.findByLogin(login)).thenReturn(java.util.Optional.of(reHubModel));
        when(mappingUtil.doMapReHubDtoAndGet(reHubModel)).thenReturn(expected);

        // Act
        RehubDto result = reHubViewService.getModelDtoByLogin(login);

        // Assert

        assertThat(result).isEqualTo(expected);

        verify(reHubRepository, times(1)).findByLogin(login);
        verify(mappingUtil, times(1)).doMapReHubDtoAndGet(reHubModel);
    }

    @Test
    void Should_LoadListOfReHubDto() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Arrange
        List<RehubDto> expectedListOfDto = new ArrayList<>();
        List<ReHubModel> expectedListOfModel = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ReHubModel reHubModel = (ReHubModel) createModel(1, ReHubModel.class);
            expectedListOfModel.add(reHubModel);
            expectedListOfDto.add(new RehubDto(
                    reHubModel.getLogin(), null, null, null, null, null, null));
        }

        when(reHubRepository.findAllBy()).thenReturn(expectedListOfModel);
        expectedListOfModel.forEach(
                model -> when(mappingUtil.doMapReHubDtoAndGet(model)).thenReturn(
                        expectedListOfDto.stream().filter(clientDto -> clientDto.login().equals(model.getLogin()))
                                .findFirst().get()));

        // Act
        List<RehubDto> result = reHubViewService.getListOfModelDto();

        // Assert
        assertThat(result).isEqualTo(expectedListOfDto);

        verify(reHubRepository, times(1)).findAllBy();
        verify(mappingUtil, times(expectedListOfDto.size())).doMapReHubDtoAndGet(any(ReHubModel.class));
    }

    // negative


    @Test
    void Should_ThrowNotFoundLoginException_When_ClientDtoByLoginNotFound() {
        // Arrange
        String nonExistingLogin = "nonExistingLogin";

        when(clientRepository.findByLogin(nonExistingLogin)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> clientViewService.getModelDtoByLogin(nonExistingLogin))
                .isInstanceOf(NotFoundLoginException.class);

        verify(clientRepository, times(1)).findByLogin(nonExistingLogin);
        verify(mappingUtil, never()).doMapClientDtoAndGet(any());
    }

    @Test
    void Should_ThrowNotFoundLoginException_When_SpecialistDtoByLoginNotFound() {
        // Arrange
        String nonExistingLogin = "nonExistingLogin";

        when(specialistRepository.findByLogin(nonExistingLogin)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> specialistViewService.getModelDtoByLogin(nonExistingLogin))
                .isInstanceOf(NotFoundLoginException.class);

        verify(specialistRepository, times(1)).findByLogin(nonExistingLogin);
        verify(mappingUtil, never()).doMapSpecialistDtoAndGet(any());
    }

    @Test
    void Should_ThrowNotFoundLoginException_When_ReHubDtoByLoginNotFound() {
        // Arrange
        String nonExistingLogin = "nonExistingLogin";

        when(reHubRepository.findByLogin(nonExistingLogin)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> reHubViewService.getModelDtoByLogin(nonExistingLogin))
                .isInstanceOf(NotFoundLoginException.class);

        verify(reHubRepository, times(1)).findByLogin(nonExistingLogin);
        verify(mappingUtil, never()).doMapReHubDtoAndGet(any());
    }


    private <T extends UserModel> UserModel createModel(int i, Class<T> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T model = type.getConstructor().newInstance();
        model.setLogin(type.getName() +"`s login " + i);
        return model;
    }
}
