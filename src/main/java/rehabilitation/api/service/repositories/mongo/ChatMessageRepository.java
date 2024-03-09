package rehabilitation.api.service.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import rehabilitation.api.service.entity.mongo.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findChatMessageByChatId(String chatId);
}
