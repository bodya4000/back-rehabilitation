package rehabilitation.api.service.services.entities;

import static org.assertj.core.api.Assertions.*;

import static org.junit.Assert.assertThrows;
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
import rehabilitation.api.service.exceptionHandling.exception.buisness.IllegalPropertyException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class UpdateEntitiesTest {
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

    // positive

    @Test
    void Should_SuccessfullyUpdateClient_When_ValidData() throws NotFoundLoginException {
        // arrange
        var client = new ClientModel();
        client.setLogin("login");
        var login = client.getLogin();
        Map<String, Object> clientUpdates = clientUpdates();

        // mock
        when(clientRepository.findByLogin(login)).thenReturn(Optional.of(client));

        // when
        clientCrudService.updateModel(login, clientUpdates);

        // assertions
        assertThat(client.getFirstName()).isEqualTo(clientUpdates.get("firstName"));
        assertThat(client.getLastName()).isEqualTo(clientUpdates.get("lastName"));
        assertThat(client.getContactInformation()).isEqualTo(clientUpdates.get("contactInformation"));
        assertThat(client.getImgUrl()).isEqualTo(clientUpdates.get("imgUrl"));
        assertThat(client.getAddress()).isEqualTo(clientUpdates.get("address"));
    }

    @Test
    void Should_SuccessfullyUpdateSpecialist_When_ValidData() throws NotFoundLoginException {
        // arrange
        var specialist = new SpecialistModel();
        specialist.setLogin("login");
        var login = specialist.getLogin();
        Map<String, Object> specialistUpdates = specialistUpdates();

        // mock
        when(specialistRepository.findByLogin(login)).thenReturn(Optional.of(specialist));

        // when
        specialistCrudService.updateModel(login, specialistUpdates);

        // assertions
        assertThat(specialist.getFirstName()).isEqualTo(specialistUpdates.get("firstName"));
        assertThat(specialist.getLastName()).isEqualTo(specialistUpdates.get("lastName"));
        assertThat(specialist.getContactInformation()).isEqualTo(specialistUpdates.get("contactInformation"));
        assertThat(specialist.getImgUrl()).isEqualTo(specialistUpdates.get("imgUrl"));
        assertThat(specialist.getAddress()).isEqualTo(specialistUpdates.get("address"));
        assertThat(specialist.getCity()).isEqualTo(specialistUpdates.get("city"));
        assertThat(specialist.getAge()).isEqualTo(specialistUpdates.get("age"));
        assertThat(specialist.getSpeciality()).isEqualTo(specialistUpdates.get("speciality"));
        assertThat(specialist.getDescription()).isEqualTo(specialistUpdates.get("description"));
    }

    @Test
    void Should_SuccessfullyUpdateReHub_When_ValidData() throws NotFoundLoginException {
        // arrange
        var reHub = new ReHubModel();
        reHub.setLogin("login");
        var login = reHub.getLogin();
        Map<String, Object> reHubUpdates = reHubUpdates();

        // mock
        when(reHubRepository.findByLogin(login)).thenReturn(Optional.of(reHub));

        // when
        reHubCrudService.updateModel(login, reHubUpdates);

        // assertions

        assertThat(reHub.getContactInformation()).isEqualTo(reHubUpdates.get("contactInformation"));
        assertThat(reHub.getImgUrl()).isEqualTo(reHubUpdates.get("imgUrl"));
        assertThat(reHub.getAddress()).isEqualTo(reHubUpdates.get("address"));
        assertThat(reHub.getCity()).isEqualTo(reHubUpdates.get("city"));
        assertThat(reHub.getName()).isEqualTo(reHubUpdates.get("name"));
    }

    // negative

    @Test
    void Should_ThrowIllegalPropertyException_When_InvalidUpdatesForClient() {
        // arrange
        var client = new ClientModel();
        client.setLogin("login");
        var login = client.getLogin();
        Map<String, Object> loginUpdate = Map.of("login", "newLogin");
        Map<String, Object> emailUpdate = Map.of("email", "newEmail");
        Map<String, Object> passwordUpdates = Map.of("password", "newPassword");
        Map<String, Object> rolesUpdate = Map.of("roles", "newRoles");
        Map<String, Object> specialistUpdate = Map.of("specialists", "newSpecialists");

        // mock
        when(clientRepository.findByLogin(login)).thenReturn(Optional.of(client));

        // when/assert
        assertThrows(IllegalPropertyException.class, () -> clientCrudService.updateModel(login, loginUpdate));
        assertThrows(IllegalPropertyException.class, () -> clientCrudService.updateModel(login, emailUpdate));
        assertThrows(IllegalPropertyException.class, () -> clientCrudService.updateModel(login, passwordUpdates));
        assertThrows(IllegalPropertyException.class, () -> clientCrudService.updateModel(login, rolesUpdate));
        assertThrows(IllegalPropertyException.class, () -> clientCrudService.updateModel(login, specialistUpdate));
    }

    @Test
    void Should_ThrowIllegalPropertyException_When_InvalidUpdatesForSpecialist() {
        // arrange
        var specialist = new SpecialistModel();
        specialist.setLogin("login");
        var login = specialist.getLogin();
        Map<String, Object> loginUpdate = Map.of("login", "newLogin");
        Map<String, Object> emailUpdate = Map.of("email", "newEmail");
        Map<String, Object> passwordUpdate = Map.of("password", "newPassword");
        Map<String, Object> rolesUpdate = Map.of("roles", "newRoles");
        Map<String, Object> clientsUpdate = Map.of("clients", "newClients");
        Map<String, Object> rateUpdate = Map.of("rate", "newRate");

        // mock
        when(specialistRepository.findByLogin(login)).thenReturn(Optional.of(specialist));

        // when/assert
        assertThrows(IllegalPropertyException.class, () -> specialistCrudService.updateModel(login, loginUpdate));
        assertThrows(IllegalPropertyException.class, () -> specialistCrudService.updateModel(login, emailUpdate));
        assertThrows(IllegalPropertyException.class, () -> specialistCrudService.updateModel(login, passwordUpdate));
        assertThrows(IllegalPropertyException.class, () -> specialistCrudService.updateModel(login, rolesUpdate));
        assertThrows(IllegalPropertyException.class, () -> specialistCrudService.updateModel(login, clientsUpdate));
        assertThrows(IllegalPropertyException.class, () -> specialistCrudService.updateModel(login, rateUpdate));
    }

    @Test
    void Should_ThrowIllegalPropertyException_When_InvalidUpdatesForReHub() {
        // arrange
        var reHub = new ReHubModel();
        reHub.setLogin("login");
        var login = reHub.getLogin();
        Map<String, Object> loginUpdate = Map.of("login", "newLogin");
        Map<String, Object> emailUpdate = Map.of("email", "newEmail");
        Map<String, Object> passwordUpdate = Map.of("password", "newPassword");
        Map<String, Object> rolesUpdate = Map.of("roles", "newRoles");
        Map<String, Object> specialistsUpdate = Map.of("specialists", "newSpecialists");
        Map<String, Object> rateUpdate = Map.of("rate", "newRate");

        // mock
        when(reHubRepository.findByLogin(login)).thenReturn(Optional.of(reHub));

        // when/assert
        assertThrows(IllegalPropertyException.class, () -> reHubCrudService.updateModel(login, loginUpdate));
        assertThrows(IllegalPropertyException.class, () -> reHubCrudService.updateModel(login, emailUpdate));
        assertThrows(IllegalPropertyException.class, () -> reHubCrudService.updateModel(login, passwordUpdate));
        assertThrows(IllegalPropertyException.class, () -> reHubCrudService.updateModel(login, rolesUpdate));
        assertThrows(IllegalPropertyException.class, () -> reHubCrudService.updateModel(login, specialistsUpdate));
        assertThrows(IllegalPropertyException.class, () -> reHubCrudService.updateModel(login, rateUpdate));
    }



    private Map<String, Object> clientUpdates() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "bohdan");
        updates.put("lastName", "shraier");
        updates.put("contactInformation", "099762482");
        updates.put("imgUrl", "ava.png");
        updates.put("address", "Lviv");
        return updates;
    }

    private Map<String, Object> specialistUpdates() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "bohdan");
        updates.put("lastName", "shraier");
        updates.put("contactInformation", "099762482");
        updates.put("imgUrl", "ava.png");
        updates.put("address", "Naukova");
        updates.put("city", "Lviv");
        updates.put("age", 12);
        updates.put("speciality", "Arms");
        updates.put("description", "Like football");
        return updates;
    }

    private Map<String, Object> reHubUpdates() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("contactInformation", "099762482");
        updates.put("imgUrl", "ava.png");
        updates.put("address", "Naukova");
        updates.put("city", "Lviv");
        updates.put("name", "reHun Ins");
        return updates;
    }
}
