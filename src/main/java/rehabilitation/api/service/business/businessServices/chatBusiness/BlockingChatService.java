package rehabilitation.api.service.business.businessServices.chatBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.entity.mongo.ChatMessage;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatUserIsBlockedException;

@Service
@RequiredArgsConstructor
public class BlockingChatService {

    private final ChatUtilService chatUtilService;

    public void checkIfBlocked(
            String senderLogin, String recipientLogin
            ) throws NotFoundLoginException, ChatUserIsBlockedException {
        var sender = chatUtilService.findByLogin(senderLogin);
        var recipient = chatUtilService.findByLogin(recipientLogin);

        throwIfBlocked(sender, recipient);
        throwIfBlocked(recipient, sender);
    }

    private void throwIfBlocked(ChatUser user1, ChatUser user2) throws ChatUserIsBlockedException {
        if (user1.getBlockedContacts().contains(user2)) {
            throw new ChatUserIsBlockedException(user1, user2);
        }
    }
}
