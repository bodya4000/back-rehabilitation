package rehabilitation.api.service.client;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import rehabilitation.api.service.config.ConfigTest;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.*;

@DataJpaTest
@Import(ConfigTest.class)
@Slf4j
class ClientRepositoryTest {

    private static final String CLIENT = "client";
    private static final String SPECIALIST = "specialist";

    @Autowired
    private GeneratingUtils generatingUtils;

    @Autowired
    private ClientRepository underTestObject;

    @Autowired
    private SpecialistRepository specialistRepository;



    @AfterEach
    void clearDb() {
        underTestObject.deleteAll();
        specialistRepository.deleteAll();
    }


    @Test
    @DisplayName("Test if clients are correctly loaded with specialists")
    void testIfClientsAreLoadedWithSpecialists() {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(CLIENT);
        SpecialistModel specialistModel = (SpecialistModel) map.get(SPECIALIST);

        // when
        List<ClientModel> clientModelList = underTestObject.findAllBy();

        // then
        assertClientsLoadedWithSpecialists(clientModelList, specialistModel);
    }

    @Test
    @DisplayName("Test if clients are found by login")
    void findByLogin() throws NotFoundLoginException {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(CLIENT);
        SpecialistModel specialistModel = (SpecialistModel) map.get(SPECIALIST);

        String clientLogin = clientModel.getLogin();

        // when
        ClientModel loadTrueClient = underTestObject.findByLogin(clientLogin).orElseThrow(() -> new NotFoundLoginException(clientLogin));

        // when/then
        assertFindByLogin(specialistModel, clientModel, loadTrueClient);

        // Additional test case with false login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }

    @Test
    @DisplayName("Test if clients are found by login and fetching its roles.")
    void findByLoginFetchRoles() throws NotFoundLoginException {
        // given
        ClientModel clientModel = generatingUtils.createClientAndSave(1);
        underTestObject.save(clientModel);
        String clientLogin = clientModel.getLogin();

        // when
        ClientModel loadTrueClient = underTestObject.findByLoginFetchRoles(clientLogin).orElseThrow(() -> new NotFoundLoginException(clientLogin));

        // then
        assertClientFetchRoles(clientModel, loadTrueClient);
    }

    private void assertClientFetchRoles(ClientModel expectedClient, ClientModel actualClient) {
        Assertions.assertThat(actualClient).satisfies(client -> {
            Assertions.assertThat(client).isEqualTo(expectedClient);
            Assertions.assertThat(client.getRoles()).isNotEmpty();
        });
    }

    private void assertFindByLogin(SpecialistModel specialistModel, ClientModel expectedClient, ClientModel actualClient) {
        Assertions.assertThat(actualClient).satisfies(client -> {
            Assertions.assertThat(client).isEqualTo(expectedClient);
            Assertions.assertThat(client.getSpecialists()).contains(specialistModel);
        });
    }

    private void assertClientsLoadedWithSpecialists(List<ClientModel> clientModelList, SpecialistModel expectedSpecialist) {
        Assertions.assertThat(clientModelList).isNotEmpty().allSatisfy(client -> {
            Assertions.assertThat(client.getSpecialists()).isNotNull();
            Assertions.assertThat(client.getSpecialists()).isNotEmpty();
            Assertions.assertThat(client.getSpecialists()).contains(expectedSpecialist);
        });
    }

    /**
     * Asserts if exception has been thrown
     *
     * @param falseLogin false value on purpose
     */
    private void assertNotFoundLoginException(String falseLogin) {
        Assertions.assertThatThrownBy(() -> underTestObject.findByLogin(falseLogin).orElseThrow(() -> new NotFoundLoginException(falseLogin))).isInstanceOf(NotFoundLoginException.class).hasMessageContaining(falseLogin);
    }
}
