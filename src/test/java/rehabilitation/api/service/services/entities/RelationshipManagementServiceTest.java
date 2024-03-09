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
public class RelationshipManagementServiceTest {
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
    void Should_AddSpecialistToClient_When_EachExistsInDB() throws NotFoundLoginException {
        // arrange
        var client = new ClientModel();
        client.setLogin("clientLogin");
        var specialist = new SpecialistModel();
        specialist.setLogin("specialistLogin");

        // mocking
        when(clientRepository.findByLogin(client.getLogin())).thenReturn(Optional.of(client));
        when(specialistRepository.findByLogin(specialist.getLogin())).thenReturn(Optional.of(specialist));

        // when
        clientCrudService.addSpecialist(client.getLogin(), specialist.getLogin());

        // assertions
        assertThat(client.getSpecialists()).contains(specialist);
        assertThat(specialist.getClients()).contains(client);
    }

    @Test
    void Should_RemoveSpecialistFromClient_When_EachExistsInDB() throws NotFoundLoginException {
        // arrange
        var client = new ClientModel();
        client.setLogin("clientLogin");
        var specialist = new SpecialistModel();
        specialist.setLogin("specialistLogin");
        client.addSpecialist(specialist);

        // mocking
        when(clientRepository.findByLogin(client.getLogin())).thenReturn(Optional.of(client));
        when(specialistRepository.findByLogin(specialist.getLogin())).thenReturn(Optional.of(specialist));

        // when
        clientCrudService.removeSpecialist(client.getLogin(), specialist.getLogin());

        // assertions
        assertThat(client.getSpecialists()).doesNotContain(specialist);
        assertThat(specialist.getClients()).doesNotContain(client);
    }

    @Test
    void Should_AddClientToSpecialist_When_EachExistsInDB() throws NotFoundLoginException {
        // arrange
        var client = new ClientModel();
        client.setLogin("clientLogin");
        var specialist = new SpecialistModel();
        specialist.setLogin("specialistLogin");

        // mocking
        when(clientRepository.findByLogin(client.getLogin())).thenReturn(Optional.of(client));
        when(specialistRepository.findByLogin(specialist.getLogin())).thenReturn(Optional.of(specialist));

        // when
        specialistCrudService.addClient(specialist.getLogin(), client.getLogin());

        // assertions
        assertThat(client.getSpecialists()).contains(specialist);
        assertThat(specialist.getClients()).contains(client);
    }

    @Test
    void Should_RemoveClientFromSpecialist_When_EachExistsInDB() throws NotFoundLoginException {
        // arrange
        var client = new ClientModel();
        client.setLogin("clientLogin");
        var specialist = new SpecialistModel();
        specialist.setLogin("specialistLogin");
        client.addSpecialist(specialist);

        // mocking
        when(clientRepository.findByLogin(client.getLogin())).thenReturn(Optional.of(client));
        when(specialistRepository.findByLogin(specialist.getLogin())).thenReturn(Optional.of(specialist));

        // when
        specialistCrudService.removeClient(specialist.getLogin(), client.getLogin());

        // assertions
        assertThat(client.getSpecialists()).doesNotContain(specialist);
        assertThat(specialist.getClients()).doesNotContain(client);
    }

    @Test
    void Should_AddSpecialistToReHUb_When_EachExistsInDB() throws NotFoundLoginException {
        // arrange
        var reHub = new ReHubModel();
        reHub.setLogin("reHubLogin");
        var specialist = new SpecialistModel();
        specialist.setLogin("specialistLogin");

        // mocking
        when(reHubRepository.findByLogin(reHub.getLogin())).thenReturn(Optional.of(reHub));
        when(specialistRepository.findByLogin(specialist.getLogin())).thenReturn(Optional.of(specialist));

        // when
        reHubCrudService.addSpecialist(reHub.getLogin(), specialist.getLogin());

        // assertions
        assertThat(reHub.getSpecialists()).contains(specialist);
        assertThat(specialist.getReHub()).isEqualTo(reHub);
    }

    @Test
    void Should_RemoveSpecialistFromReHub_When_EachExistsInDB() throws NotFoundLoginException {
        // arrange
        var reHub = new ReHubModel();
        reHub.setLogin("reHubLogin");
        var specialist = new SpecialistModel();
        specialist.setLogin("specialistLogin");
        reHub.removeSpecialist(specialist);


        // mocking
        when(reHubRepository.findByLogin(reHub.getLogin())).thenReturn(Optional.of(reHub));
        when(specialistRepository.findByLogin(specialist.getLogin())).thenReturn(Optional.of(specialist));

        // when
        reHubCrudService.removeSpecialist(reHub.getLogin(), specialist.getLogin());

        // assertions
        assertThat(reHub.getSpecialists()).isEmpty();
        assertThat(specialist.getReHub()).isNull();
    }


}
