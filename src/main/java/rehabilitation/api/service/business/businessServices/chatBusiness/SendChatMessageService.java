package rehabilitation.api.service.business.businessServices.chatBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.chat.ChatMessageDto;
import rehabilitation.api.service.entity.mongo.ChatMessage;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatUserIsBlockedException;
import rehabilitation.api.service.repositories.mongo.ChatMessageRepository;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class SendChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatUserService chatUserService;
    private final BlockingChatService blockingChatService;

    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto) throws NotFoundLoginException, ChatUserIsBlockedException {
        blockingChatService.checkIfBlocked(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        ensureChatAndContactsExistForUsers(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        ChatMessage message = saveMessage(chatMessageDto);
        notifyRecipient(message);
    }

    private void ensureChatAndContactsExistForUsers(String senderLogin, String recipientLogin)
            throws NotFoundLoginException {
        if (chatRoomService.checkIfChatRoomDoesNotExists(senderLogin, recipientLogin)){
            chatRoomService.createNewChatRooms(senderLogin, recipientLogin);
            chatUserService.addUserContact(senderLogin, recipientLogin);
        }
    }

    private void notifyRecipient(ChatMessage message) {
        messagingTemplate.convertAndSend(
                "/queue/"+message.getRecipientLogin()+"/messages",
                message
        );
    }



    private ChatMessage saveMessage(ChatMessageDto chatMessageDto){
        var message = new ChatMessage();
        message.setSenderLogin(chatMessageDto.senderLogin());
        message.setRecipientLogin(chatMessageDto.recipientLogin());
        message.setContent(chatMessageDto.content());
        message.setChatId(chatMessageDto.chatId());
        message.setTimestamp(LocalDate.now());
        return chatMessageRepository.save(message);
    }

}
