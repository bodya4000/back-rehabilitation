package rehabilitation.api.service.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatRoomService;
import rehabilitation.api.service.business.businessServices.chatBusiness.SendChatMessageService;
import rehabilitation.api.service.dto.chat.ChatMessageDto;
import rehabilitation.api.service.entity.mongo.ChatMessage;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatNotFoundException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatUserIsBlockedException;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SendChatMessageService sendChatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat.sendMessage")
    public void onReceivingMessage(@Payload ChatMessageDto chatMessageDto)
            throws NotFoundLoginException, ChatUserIsBlockedException {
        sendChatMessageService.sendMessage(chatMessageDto);
    }

    @GetMapping("/messages/{senderLogin}/{recipientLogin}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @PathVariable String senderLogin,
            @PathVariable String recipientLogin) throws ChatNotFoundException {
        return ResponseEntity.ok(
                chatRoomService.findMessagesInChatRoom(senderLogin, recipientLogin)
        );
    }

}
