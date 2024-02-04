package rehabilitation.api.service.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import rehabilitation.api.service.entity.mongo.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findChatRoomBySenderLoginAndReceiverLogin(String senderLogin, String recipientLogin);

    boolean existsChatRoomBySenderLoginAndReceiverLogin(String senderLogin, String recipientLogin);
}
