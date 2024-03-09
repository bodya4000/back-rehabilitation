package rehabilitation.api.service.repositories.mongo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;


@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
public class ChatUserRepositoryTest {
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
    private MongoTemplate mongoTemplate;

    @Autowired
    private ChatUserRepository underTheTest;

    @BeforeEach
    public void clearDb(){
        mongoTemplate.getDb().drop();
    }

    @Test
    void Should_FindChatUser_When_LoginCorrect() throws NotFoundLoginException {
        // arrange
        var expected = createChatUser();
        underTheTest.save(expected);

        // when
        ChatUser result = underTheTest.findChatUserByLogin(expected.getLogin())
                .orElseThrow(() -> new NotFoundLoginException(expected.getLogin()));

        // assertions
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void Should_ReturnTrue_When_ChatUserExistsByLogin() {
        // arrange
        var chatUser = createChatUser();
        underTheTest.save(chatUser);

        // when
        boolean result = underTheTest.existsChatUserByLogin(chatUser.getLogin());

        // assertions
        assertThat(result).isTrue();
    }

    @Test
    void Should_ReturnFalse_When_ChatUserDoesNotExistByLogin() {
        // arrange
        var nonExistingLogin = "nonExistingLogin";

        // when
        boolean result = underTheTest.existsChatUserByLogin(nonExistingLogin);

        // assertions
        assertThat(result).isFalse();
    }



    private ChatUser createChatUser() {
        var chatUser = new ChatUser();
        chatUser.setLogin("login");
        return chatUser;
    }
}
