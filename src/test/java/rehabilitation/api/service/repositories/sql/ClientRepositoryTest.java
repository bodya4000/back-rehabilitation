package rehabilitation.api.service.repositories.sql;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.utills.EntityType;
import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.*;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
class ClientRepositoryTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:jammy")
            .withExposedPorts(27017);

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine");

    static Network network;
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:alpine")
            .withExposedPorts(6379)
            .withNetwork(network);


    @DynamicPropertySource
    public static void setUpThings(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgreSQLContainer, mongoDBContainer, redisContainer).join();

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    static {
        mongoDBContainer.start();
        var mappedPort = mongoDBContainer.getMappedPort(27017);
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
    }
    @Autowired
    private GeneratingUtils generatingUtils;

    @Autowired
    private ClientRepository underTestObject;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void clearDb() {
        entityManager.clear();
    }

    @Test
    void Should_FindAllClients() {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        SpecialistModel specialistModel = (SpecialistModel) map.get(EntityType.SPECIALIST);

        // when
        List<ClientModel> clientModelList = underTestObject.findAllBy();

        // then
        assertClientsLoadedWithSpecialists(clientModelList, specialistModel);
    }

    @Test
    void Should_FindClientWithSpecialists_When_LoginCorrect() throws NotFoundLoginException {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(EntityType.CLIENT);
        SpecialistModel specialistModel = (SpecialistModel) map.get(EntityType.SPECIALIST);

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
    void Should_FindClientWithRoles_When_LoginCorrect() throws NotFoundLoginException {
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
