package rehabilitation.api.service.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import rehabilitation.api.service.entity.mongo.ChatUser;

import java.util.Optional;

public interface ChatUserRepository extends MongoRepository<ChatUser, String> {

    Optional<ChatUser> findChatUserByLogin(String login);

    boolean existsChatUserByLogin(String login);
}