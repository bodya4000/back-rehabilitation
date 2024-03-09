package rehabilitation.api.service.services.business.chat;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rehabilitation.api.service.business.businessServices.chatBusiness.BlockingChatService;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatRoomService;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatUserService;
import rehabilitation.api.service.business.businessServices.chatBusiness.SendChatMessageService;
import rehabilitation.api.service.dto.chat.ChatMessageDto;
import rehabilitation.api.service.entity.mongo.ChatMessage;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatUserIsBlockedException;
import rehabilitation.api.service.repositories.mongo.ChatMessageRepository;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class SendMessageServiceTest {
    @InjectMocks
    private SendChatMessageService underTheTest;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private ChatUserService chatUserService;
    @Mock
    private BlockingChatService blockingChatService;


    @Test
    void Should_SendMessageSuccessfully_When_ChatRoomExists() throws NotFoundLoginException, ChatUserIsBlockedException {
        // Arrange
        var chatMessageDto = new ChatMessageDto("senderLogin", "recipientLogin", "hello", "senderLogin_recipientLogin");
        var chatMessage = createChatMessage(chatMessageDto);

        when(chatRoomService.checkIfChatRoomDoesNotExists(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin()))
                .thenReturn(false);
        when(chatMessageRepository.save(chatMessage)).thenReturn(chatMessage);

        // Act
        underTheTest.sendMessage(chatMessageDto);

        // Assert
        verify(blockingChatService).checkIfBlocked(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatRoomService).checkIfChatRoomDoesNotExists(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatMessageRepository).save(chatMessage);
        verify(messagingTemplate).convertAndSend("/queue/" + chatMessageDto.recipientLogin() + "/messages", chatMessage);
        verify(chatRoomService, never()).createNewChatRooms(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatUserService, never()).addUserContact(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
    }

    @Test
    void Should_SendMessageSuccessfully_When_ChatRoomNotExists() throws NotFoundLoginException, ChatUserIsBlockedException {
        // Arrange
        var chatMessageDto = new ChatMessageDto("senderLogin", "recipientLogin", "hello", "senderLogin_recipientLogin");
        var chatMessage = createChatMessage(chatMessageDto);

        when(chatRoomService.checkIfChatRoomDoesNotExists(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin()))
                .thenReturn(true);
        when(chatMessageRepository.save(chatMessage)).thenReturn(chatMessage);

        // Act
        underTheTest.sendMessage(chatMessageDto);

        // Assert
        verify(blockingChatService).checkIfBlocked(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatRoomService).checkIfChatRoomDoesNotExists(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatRoomService).createNewChatRooms(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatUserService).addUserContact(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());
        verify(chatMessageRepository).save(chatMessage);
        verify(messagingTemplate).convertAndSend("/queue/" + chatMessageDto.recipientLogin() + "/messages", chatMessage);
    }

    @Test
    void Should_ThrowChatUserIsBlockedException_When_UserBlocked() throws NotFoundLoginException, ChatUserIsBlockedException {
        // Arrange
        var chatMessageDto = new ChatMessageDto("senderLogin", "recipientLogin", "hello", "senderLogin_recipientLogin");

        doThrow(new ChatUserIsBlockedException(chatMessageDto.recipientLogin(), chatMessageDto.senderLogin()))
                .when(blockingChatService).checkIfBlocked(chatMessageDto.senderLogin(), chatMessageDto.recipientLogin());

        // Act and Assert
        assertThatThrownBy(() -> underTheTest.sendMessage(chatMessageDto))
                .isInstanceOf(ChatUserIsBlockedException.class);    }

    private ChatMessage createChatMessage(ChatMessageDto chatMessageDto){
        var message = new ChatMessage();
        message.setSenderLogin(chatMessageDto.senderLogin());
        message.setRecipientLogin(chatMessageDto.recipientLogin());
        message.setContent(chatMessageDto.content());
        message.setChatId(chatMessageDto.chatId());
        message.setTimestamp(LocalDate.now());
        return message;
    }


}
