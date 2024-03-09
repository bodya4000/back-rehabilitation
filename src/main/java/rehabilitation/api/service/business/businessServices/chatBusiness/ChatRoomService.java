package rehabilitation.api.service.business.businessServices.chatBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.entity.mongo.ChatMessage;
import rehabilitation.api.service.entity.mongo.ChatRoom;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatNotFoundException;
import rehabilitation.api.service.repositories.mongo.ChatMessageRepository;
import rehabilitation.api.service.repositories.mongo.ChatRoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    
    private final ChatUtilService chatUtilService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> findMessagesInChatRoom(
            String senderLogin, String recipientLogin
    ) throws ChatNotFoundException {
        var chatRoom = chatRoomRepository.findChatRoomBySenderLoginAndReceiverLogin(senderLogin, recipientLogin)
                .orElseThrow(ChatNotFoundException::new);
        return chatMessageRepository.findChatMessageByChatId(chatRoom.getChatId());
    }

    public void createNewChatRooms(String firstUserLogin, String secondUserLogin) {
        var chatId = String.format("%s_%s", firstUserLogin, secondUserLogin);
        var firstSecondChatRoom = chatUtilService.createChatRoom(firstUserLogin, secondUserLogin, chatId);
        var secondFirstChatRoom = chatUtilService.createChatRoom(secondUserLogin, firstUserLogin, chatId);
        chatRoomRepository.saveAll(List.of(firstSecondChatRoom, secondFirstChatRoom));
    }
    public boolean checkIfChatRoomDoesNotExists(String login1, String login2) {
        return !chatRoomRepository.existsChatRoomBySenderLoginAndReceiverLogin(login1, login2)
                && !chatRoomRepository.existsChatRoomBySenderLoginAndReceiverLogin(login2, login1);
    }
}
