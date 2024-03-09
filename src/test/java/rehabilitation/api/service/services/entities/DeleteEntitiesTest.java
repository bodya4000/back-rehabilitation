package rehabilitation.api.service.services.entities;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import rehabilitation.api.service.business.businessServices.clientBusiness.crud.ClientCrudService;
import rehabilitation.api.service.business.businessServices.reHubBusiness.crud.ReHubCrudService;
import rehabilitation.api.service.business.businessServices.specialistBusiness.crud.SpecialistCrudService;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DeleteEntitiesTest {
    private ClientCrudService clientCrudService;
    private SpecialistCrudService specialistCrudService;
    private ReHubCrudService reHubCrudService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private ReHubRepository reHubRepository;

    @BeforeEach
    public void setUp() {
        clientCrudService = new ClientCrudService(specialistRepository, clientRepository);
        specialistCrudService = new SpecialistCrudService(specialistRepository, clientRepository);
        reHubCrudService = new ReHubCrudService(specialistRepository, reHubRepository);
    }

    @Test
    void Should_DeleteClient_When_ExistsInDB() throws NotFoundLoginException {
        // arrange
        var client = new ClientModel();
        client.setLogin("client login");
        var login = client.getLogin();
        client.addSpecialist(new SpecialistModel());

        // mocking
        when(clientRepository.findByLogin(login)).thenReturn(Optional.of(client));

        // when
        clientCrudService.deleteModel(client.getLogin());

        // assertions
        assertThat(client.getSpecialists()).isEmpty();
        verify(clientRepository).delete(client);
    }

    @Test
    void Should_DeleteSpecialist_When_ExistsInDB() throws NotFoundLoginException {
        // arrange
        var specialist = new SpecialistModel();
        specialist.setLogin("specialist login");
        var login = specialist.getLogin();
        specialist.addClient(new ClientModel());
        specialist.setReHub(new ReHubModel());

        // mocking
        when(specialistRepository.findByLogin(login)).thenReturn(Optional.of(specialist));

        // when
        specialistCrudService.deleteModel(specialist.getLogin());

        // assertions
        assertThat(specialist.getClients()).isEmpty();
        assertThat(specialist.getReHub()).isNull();
        verify(specialistRepository).delete(specialist);
    }

    @Test
    void Should_DeleteReHub_When_ExistsInDB() throws NotFoundLoginException {
        // arrange
        var reHub = new ReHubModel();
        reHub.setLogin("reHub login");
        var login = reHub.getLogin();
        reHub.addSpecialist(new SpecialistModel());

        // mocking
        when(reHubRepository.findByLogin(login)).thenReturn(Optional.of(reHub));

        // when
        reHubCrudService.deleteModel(reHub.getLogin());

        // assertions
        assertThat(reHub.getSpecialists()).isEmpty();
        verify(reHubRepository).delete(reHub);
    }


}
