package rehabilitation.api.service.repositories.sql;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.utills.EntityType;
import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.List;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
class ReHubRepositoryTest  {

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
    private ReHubRepository underTestObject;

    @Autowired
    private GeneratingUtils generatingUtils;

    @AfterEach
    void clearDb() {
        entityManager.clear();
    }


    @Test
    void Should_FindAllReHubsWithSpecialistsAndClients() {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(EntityType.CLIENT);
        SpecialistModel specialistModel = (SpecialistModel) map.get(EntityType.SPECIALIST);

        // when
        List<ReHubModel> reHubModelList = underTestObject.findAllBy();

        // then
        assertReHubsLoadedWithSpecialistsAndClients(reHubModelList, specialistModel, clientModel);
    }

    @Test
    void Should_FindReHubWithSpecialistsAndClients_When_LoginCorrect() throws NotFoundLoginException {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ClientModel clientModel = (ClientModel) map.get(EntityType.CLIENT);
        ReHubModel reHubModel = (ReHubModel) map.get(EntityType.REHUB);
        SpecialistModel specialistModel = (SpecialistModel) map.get(EntityType.SPECIALIST);

        String reHubLogin = reHubModel.getLogin();

        // when
        ReHubModel loadTrueReHub = underTestObject.findByLogin(reHubLogin)
                .orElseThrow(() -> new NotFoundLoginException(reHubLogin));

        // when/then
        assertFindByLogin(loadTrueReHub, reHubModel);

        //then
        assertReHubsLoadedWithSpecialistsAndClients(reHubModel, specialistModel, clientModel);

        // Additional test case with false login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }

    @Test
    @DisplayName("Test finding a rehub by login and fetching its roles.")
    void Should_FindReHubWithRoles_When_LoginCorrect() throws NotFoundLoginException {
        // given
        var map = generatingUtils.createClientAndSpecialistAndSave(1);
        ReHubModel reHubModel = (ReHubModel) map.get(EntityType.REHUB);
        String reHubLogin = reHubModel.getLogin();

        // when
        ReHubModel loadTrueReHub = underTestObject.findByLoginFetchRoles(reHubLogin)
                .orElseThrow(() -> new NotFoundLoginException(reHubLogin));

        // then
        assertReHubFetchRoles(loadTrueReHub);

        // Additional test case with false login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }

    /**
     * Asserts if specialist has been loaded with roles
     *
     * @param loadedReHub the specialist that has been loaded from db
     */
    private void assertReHubFetchRoles(ReHubModel loadedReHub) {
        Assertions.assertThat(loadedReHub).satisfies(
                reHub -> {
                    Assertions.assertThat(reHub.getRoles()).isNotEmpty().isNotNull();
                }
        );
    }

    /**
     * Asserts if specialist have been loaded with clients from db
     *
     * @param loadedFromDbReHub the specialist that has been loaded from db
     * @param actualRehub       specialist that we created and saved in db
     */
    private void assertFindByLogin(
            ReHubModel loadedFromDbReHub,
            ReHubModel actualRehub
    ) {
        Assertions.assertThat(loadedFromDbReHub).satisfies(
                reHub -> {
                    Assertions.assertThat(reHub).isEqualTo(actualRehub);
                }
        );
    }

    /**
     * Asserts if each specialist have been loaded with his clients
     *
     * @param reHubModelList the list we have loaded from db
     * @param expectedClient the client that we want one of the specialist to have
     */
    private void assertReHubsLoadedWithSpecialistsAndClients(
            List<ReHubModel> reHubModelList,
            SpecialistModel expectedSpecialist,
            ClientModel expectedClient) {

        Assertions.assertThat(reHubModelList).isNotEmpty().allSatisfy(rehub -> {
            Assertions.assertThat(rehub.getSpecialists()).allSatisfy(specialist -> {
                Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                Assertions.assertThat(specialist.getClients()).contains(expectedClient);
            });
        });
    }

    private void assertReHubsLoadedWithSpecialistsAndClients(
            ReHubModel reHubModel,
            SpecialistModel expectedSpecialist,
            ClientModel expectedClient) {

        Assertions.assertThat(reHubModel.getSpecialists()).isNotNull().isNotEmpty();
        Assertions.assertThat(reHubModel.getSpecialists()).contains(expectedSpecialist).allSatisfy(
                specialist -> {
                    Assertions.assertThat(specialist.getClients()).isNotEmpty().isNotNull();
                    Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                    Assertions.assertThat(specialist.getClients()).contains(expectedClient);
                }
        );
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
