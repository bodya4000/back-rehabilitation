package rehabilitation.api.service.services.business.chat;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatRoomService;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatUtilService;
import rehabilitation.api.service.entity.mongo.ChatMessage;
import rehabilitation.api.service.entity.mongo.ChatRoom;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatNotFoundException;
import rehabilitation.api.service.repositories.mongo.ChatMessageRepository;
import rehabilitation.api.service.repositories.mongo.ChatRoomRepository;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@TestComponent
@ComponentScan("rehabilitation.api.service.business.businessServices.chatBusiness")
public class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService underTheTest;

    @Mock
    private ChatUtilService chatUtilService;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Test
    void Should_LoadChatRoomMessages_When_ChatRoomExists() throws ChatNotFoundException {
        // given
        var chatRoom = chatRoom("first", "second", "first_second");
        var chatRoomMessages = createSomeMessages(chatRoom);
        // when
        when(chatRoomRepository.
                findChatRoomBySenderLoginAndReceiverLogin(chatRoom.getSenderLogin(), chatRoom.getReceiverLogin()))
                .thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findChatMessageByChatId(chatRoom.getChatId())).thenReturn(chatRoomMessages);

        // then
        List<ChatMessage> result = underTheTest.findMessagesInChatRoom(chatRoom.getSenderLogin(), chatRoom.getReceiverLogin());
        assertThat(result).isEqualTo(chatRoomMessages);

        verify(chatRoomRepository, times(1)).
                findChatRoomBySenderLoginAndReceiverLogin(chatRoom.getSenderLogin(), chatRoom.getReceiverLogin());
        verify(chatMessageRepository, times(1)).
                findChatMessageByChatId(chatRoom.getChatId());
    }

    @Test
    void Should_ThrowChatNotFoundException_When_ChatRoomNotExist() {
        // given
        var chatRoom = chatRoom("first", "second", "first_second");
        // when
        when(chatRoomRepository.
                findChatRoomBySenderLoginAndReceiverLogin(chatRoom.getSenderLogin(), chatRoom.getReceiverLogin()))
                .thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> underTheTest.findMessagesInChatRoom(chatRoom.getSenderLogin(), chatRoom.getReceiverLogin()))
                .isInstanceOf(ChatNotFoundException.class);

        // verification
        verify(chatRoomRepository, times(1)).
                findChatRoomBySenderLoginAndReceiverLogin(chatRoom.getSenderLogin(), chatRoom.getReceiverLogin());
        verify(chatMessageRepository, times(0)).
                findChatMessageByChatId(chatRoom.getChatId());
    }

    @Test
    void Should_CreateChatRoom_When_EachChatUserExists() {
        // given
        var firstChatUserLogin = "firstLogin";
        var secondChatUserLogin = "secondLogin";
        var chatId = String.format("%s_%s", firstChatUserLogin, secondChatUserLogin);
        var rooms = createChatRooms(firstChatUserLogin, secondChatUserLogin, chatId);
        var roomForFirst = rooms.get(firstChatUserLogin);
        var roomForSecond = rooms.get(secondChatUserLogin);

        when(chatUtilService.createChatRoom(firstChatUserLogin, secondChatUserLogin, chatId))
                .thenReturn(roomForFirst);

        when(chatUtilService.createChatRoom(secondChatUserLogin, firstChatUserLogin, chatId))
                .thenReturn(roomForSecond);

        // when
        underTheTest.createNewChatRooms(firstChatUserLogin, secondChatUserLogin);

        // then
        verify(chatRoomRepository, times(1)).saveAll(
                List.of(roomForFirst, roomForSecond));
    }



    private List<ChatMessage> createSomeMessages(ChatRoom chatRoom){
        List<ChatMessage> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setChatId(chatRoom.getChatId());
            chatMessage.setSenderLogin(chatRoom.getSenderLogin());
            chatMessage.setRecipientLogin(chatRoom.getReceiverLogin());
            chatMessage.setContent("message " + i);
            messages.add(chatMessage);
        }
        return messages;
    }

    private Map<String, ChatRoom> createChatRooms(String firstLogin, String secondLogin, String chatId) {
        var map = new HashMap<String, ChatRoom>();
        var chatRoomForFirstUser =  chatRoom(firstLogin, secondLogin, chatId);
        var chatRoomForSecondUser = chatRoom(secondLogin, firstLogin, chatId);
        map.put(firstLogin, chatRoomForFirstUser);
        map.put(secondLogin, chatRoomForSecondUser);
        return map;
    }

    private ChatRoom chatRoom(
            String firstLogin, String secondLogin, String chatId
    ) {
        return ChatRoom.builder()
                .receiverLogin(secondLogin)
                .senderLogin(firstLogin)
                .chatId(chatId)
                .build();
    }
}
