package rehabilitation.api.service.services.business.chat;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import rehabilitation.api.service.business.businessServices.chatBusiness.BlockingChatService;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatUtilService;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.chat.ChatUserIsBlockedException;


@ExtendWith(MockitoExtension.class)
public class BlockingChatServiceTest {
    @InjectMocks
    private BlockingChatService underTheTest;
    @Mock
    private ChatUtilService chatUtilService;

    @Test
    void Should_NotThrowChatUserIsBlockedException_When_UserNotBlocked() throws NotFoundLoginException, ChatUserIsBlockedException {
        // given
        var firstUser = chatUser(1);
        var secondUser = chatUser(2);

        // when
        when(chatUtilService.findByLogin(firstUser.getLogin())).thenReturn(firstUser);
        when(chatUtilService.findByLogin(secondUser.getLogin())).thenReturn(secondUser);

        // then
        underTheTest.checkIfBlocked(firstUser.getLogin(), secondUser.getLogin());
    }

    @Test
    void Should_ThrowChatUserIsBlockedException_When_UserBlocked() throws NotFoundLoginException, ChatUserIsBlockedException {
        // given
        var firstUser = chatUser(1);
        var secondUser = chatUser(2);
        firstUser.getBlockedContacts().add(secondUser);

        // when
        when(chatUtilService.findByLogin(firstUser.getLogin())).thenReturn(firstUser);
        when(chatUtilService.findByLogin(secondUser.getLogin())).thenReturn(secondUser);

        // then
        assertThatThrownBy(() -> underTheTest.checkIfBlocked(firstUser.getLogin(), secondUser.getLogin()))
                .isInstanceOf(ChatUserIsBlockedException.class);
    }

    private ChatUser chatUser(int i) {
        var user = new ChatUser();
        user.setLogin("login " + i);
        return user;
    }

}
