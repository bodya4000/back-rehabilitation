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
import rehabilitation.api.service.entity.mongo.ChatMessage;

import java.util.List;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
public class ChatMessageRepositoryTest {
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
    private ChatMessageRepository underTheTest;

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }
    @Test
    void Should_FindChatMessagesByChatId() {
        // arrange
        String chatId = "testChatId";
        ChatMessage message1 = createChatMessage(chatId);
        ChatMessage message2 = createChatMessage(chatId);
        ChatMessage message3 = createChatMessage("differentChatId");
        underTheTest.saveAll(List.of(message1, message2, message3));

        // when
        List<ChatMessage> result = underTheTest.findChatMessageByChatId(chatId);

        // assertions
        assertThat(result).containsExactlyInAnyOrder(message1, message2);
    }

    @Test
    void Should_ReturnEmptyList_When_NoChatMessagesFoundByChatId() {
        // arrange
        String nonExistingChatId = "nonExistingChatId";

        // when
        List<ChatMessage> result = underTheTest.findChatMessageByChatId(nonExistingChatId);

        // assertions
        assertThat(result).isEmpty();
    }

    private ChatMessage createChatMessage(String chatId) {
        var chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        // set other properties as needed
        return chatMessage;
    }
}