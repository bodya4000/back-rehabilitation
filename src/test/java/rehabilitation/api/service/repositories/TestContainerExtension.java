package rehabilitation.api.service.repositories;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
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

import java.util.List;

public class TestContainerExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Network network = Network.newNetwork();

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:jammy")
            .withExposedPorts(27017)
            .withNetwork(network);

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine")
            .withNetwork(network);

    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:alpine")
            .withExposedPorts(6379)
            .withNetwork(network);

    static {
        Startables.deepStart(postgreSQLContainer, mongoDBContainer, redisContainer);
        // Additional setup if needed
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // Additional setup before all tests, if needed
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // Additional teardown after all tests, if needed
        Stopables.deepStop(postgreSQLContainer, mongoDBContainer, redisContainer);
    }

    public static Network getNetwork() {
        return network;
    }

    public static MongoDBContainer getMongoDBContainer() {
        return mongoDBContainer;
    }

    public static PostgreSQLContainer<?> getPostgreSQLContainer() {
        return postgreSQLContainer;
    }

    public static GenericContainer<?> getRedisContainer() {
        return redisContainer;
    }

    // Additional methods to retrieve container information or perform other setup/teardown actions

    private static class Startables {
        static void deepStart(GenericContainer<?>... containers) {
            List.of(containers).forEach(GenericContainer::start);
        }
    }

    private static class Stopables {
        static void deepStop(GenericContainer<?>... containers) {
            List.of(containers).forEach(GenericContainer::stop);
        }
    }
}