package rehabilitation.api.service.business.businessServices.chatBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.entity.mongo.ChatRoom;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class ChatUtilService {

    private final ChatUserRepository chatUserRepository;

    public ChatUser findByLogin(String login) throws NotFoundLoginException {
        return chatUserRepository.findChatUserByLogin(login)
                .orElseThrow(() -> new NotFoundLoginException(login));
    }

    public ChatRoom createChatRoom(String firstUserLogin, String secondUserLogin, String chatId) {
        var firstSecondChatRoom = new ChatRoom();
        firstSecondChatRoom.setChatId(chatId);
        firstSecondChatRoom.setSenderLogin(firstUserLogin);
        firstSecondChatRoom.setReceiverLogin(secondUserLogin);
        return firstSecondChatRoom;
    }
}
