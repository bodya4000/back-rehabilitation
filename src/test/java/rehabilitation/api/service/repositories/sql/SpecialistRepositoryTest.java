package rehabilitation.api.service.repositories.sql;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.utills.EntityType;
import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.List;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
class SpecialistRepositoryTest  {
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
    private EntityManager entityManager;

    @Autowired
    private SpecialistRepository underTestObject;

    @Autowired
    private GeneratingUtils generatingUtils;

    @AfterEach
    void clearDb() {
        entityManager.clear();
    }


    @Test
    @DisplayName("Test if specialists are correctly loaded with clients and with reHub")
    void Should_FindAllSpecialistsWithClientsAndReHub() {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(EntityType.CLIENT);
        ReHubModel reHubModel = (ReHubModel) map.get(EntityType.REHUB);

        // when
        List<SpecialistModel> specialistModels = underTestObject.findAllBy();

        // then
        assertSpecialistsLoadedWithClients(specialistModels, clientModel);
        assertSpecialistsLoadedWithRehub(specialistModels, reHubModel);
    }

    @Test
    @DisplayName("Test if specialists are correctly loaded with clients and with reHub by login")
    void Should_FindSpecialistWithClientsAndReHub_When_LoginCorrect() throws NotFoundLoginException {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(EntityType.CLIENT);
        ReHubModel reHubModel = (ReHubModel) map.get(EntityType.REHUB);
        SpecialistModel specialistModel = (SpecialistModel) map.get(EntityType.SPECIALIST);

        String specialistLogin = specialistModel.getLogin();

        // when
        SpecialistModel loadTrueSpecialist = underTestObject.findByLogin(specialistLogin)
                .orElseThrow(() -> new NotFoundLoginException(specialistLogin));

        // when/then
        assertFindByLogin(clientModel, specialistModel, loadTrueSpecialist);

        // then

        assertSpecialistsLoadedWithClients(specialistModel, clientModel);
        assertSpecialistsLoadedWithRehub(specialistModel, reHubModel);

        // Additional test case with false login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }

    @Test
    @DisplayName("Test finding a specialist by login and fetching its roles.")
    void Should_FindSpecialistWithRoles_When_LoginCorrect() throws NotFoundLoginException {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        SpecialistModel specialistModel = (SpecialistModel) map.get(EntityType.SPECIALIST);
        String specialistLogin = specialistModel.getLogin();

        // when
        SpecialistModel loadTrueSpecialist = underTestObject.findByLoginFetchRoles(specialistLogin)
                .orElseThrow(() -> new NotFoundLoginException(specialistLogin));

        // then
        assertSpecialistFetchRoles(specialistModel, loadTrueSpecialist);

        // Additional test case with false login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }


    private void assertSpecialistFetchRoles(SpecialistModel expectedSpecialist, SpecialistModel actualSpecialist) {
        Assertions.assertThat(actualSpecialist).satisfies(
                specialist -> {
                    Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                    Assertions.assertThat(specialist.getRoles()).isNotEmpty();
                }
        );
    }

    private void assertFindByLogin(ClientModel clientModel,
                                   SpecialistModel expectedSpecialist, SpecialistModel actualSpecialist) {
        Assertions.assertThat(actualSpecialist).satisfies(
                specialist -> {
                    Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                    Assertions.assertThat(specialist.getClients()).contains(clientModel);
                }
        );
    }

    /**
     * Asserts list of all specialists in db if they were loaded with clients
     *
     * @param specialistModelList the list that is being asserted
     * @param expectedClient      the client that we expect one of the specialist to have
     */

    private void assertSpecialistsLoadedWithClients(List<SpecialistModel> specialistModelList, ClientModel expectedClient) {
        Assertions.assertThat(specialistModelList).isNotEmpty().allSatisfy(specialist -> {
            Assertions.assertThat(specialist.getClients()).isNotNull();
            Assertions.assertThat(specialist.getClients()).isNotEmpty();
            Assertions.assertThat(specialist.getClients()).contains(expectedClient);
        });
    }

    /**
     * Asserts a specific specialist loaded by login in db if he was loaded with clients
     *
     * @param specialist     specialist that is being asserted
     * @param expectedClient the client that we expect one of the specialist to have
     */

    private void assertSpecialistsLoadedWithClients(SpecialistModel specialist, ClientModel expectedClient) {
        Assertions.assertThat(specialist.getClients()).isNotNull();
        Assertions.assertThat(specialist.getClients()).isNotEmpty();
        Assertions.assertThat(specialist.getClients()).contains(expectedClient);
    }

    /**
     * Asserts list of all specialists in db if they were loaded with rehub
     *
     * @param specialistModelList the list that is being asserted
     * @param reHubModel          the rehub that we expect one of the specialist to have
     */
    private void assertSpecialistsLoadedWithRehub(List<SpecialistModel> specialistModelList, ReHubModel reHubModel) {
        Assertions.assertThat(specialistModelList).isNotEmpty().allSatisfy(specialist -> {
            Assertions.assertThat(specialist.getReHub()).isEqualTo(reHubModel);
        });
    }

    /**
     * Asserts a specific specialist loaded by login in db if he was loaded with rehub
     *
     * @param specialist specialist that is being asserted
     * @param reHubModel the rehub that we expect one of the specialist to have
     */
    private void assertSpecialistsLoadedWithRehub(SpecialistModel specialist, ReHubModel reHubModel) {
        Assertions.assertThat(specialist.getReHub()).isEqualTo(reHubModel);
    }


    /**
     * Asserts if exception has been thrown
     *
     * @param falseLogin false value on purpose
     */
    private void assertNotFoundLoginException(String falseLogin) {
        Assertions.assertThatThrownBy(() ->
                        underTestObject.findByLogin(falseLogin)
                                .orElseThrow(() -> new NotFoundLoginException(falseLogin))
                )
                .isInstanceOf(NotFoundLoginException.class)
                .hasMessageContaining(falseLogin);
    }
}
