package rehabilitation.api.service.repositories.mongo;

import org.assertj.core.api.Assertions;
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
import rehabilitation.api.service.entity.mongo.ChatRoom;

import java.util.Optional;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
public class ChatRoomRepositoryTest {
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
    private ChatRoomRepository underTheTest;

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }

    @Test
    void Should_FindChatRoomBySenderLoginAndReceiverLogin_When_ChatRoomExists() {
        // arrange
        var chatRoom = new ChatRoom();
        chatRoom.setSenderLogin("senderLogin");
        chatRoom.setReceiverLogin("recipientLogin");
        underTheTest.save(chatRoom);

        // when
        Optional<ChatRoom> result = underTheTest.findChatRoomBySenderLoginAndReceiverLogin(
                chatRoom.getSenderLogin(), chatRoom.getReceiverLogin());

        // assertions
        Assertions.assertThat(chatRoom).isEqualTo(result.get());
    }

    @Test
    void Should_SuccessFullyCheckIfChatRoomBySenderLoginAndReceiverLoginExists() {
        // arrange
        var chatRoom = new ChatRoom();
        chatRoom.setSenderLogin("senderLogin");
        chatRoom.setReceiverLogin("recipientLogin");
        underTheTest.save(chatRoom);

        // when
        boolean result = underTheTest.existsChatRoomBySenderLoginAndReceiverLogin(
                chatRoom.getSenderLogin(), chatRoom.getReceiverLogin());

        // assertions
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void Should_UnSuccessFullyCheckIfChatRoomBySenderLoginAndReceiverLoginExists() {
        // arrange
        var chatRoom = new ChatRoom();
        chatRoom.setSenderLogin("senderLogin");
        chatRoom.setReceiverLogin("recipientLogin");

        // when
        boolean result = underTheTest.existsChatRoomBySenderLoginAndReceiverLogin(
                chatRoom.getSenderLogin(), chatRoom.getReceiverLogin());

        // assertions
        Assertions.assertThat(result).isEqualTo(false);
    }
}