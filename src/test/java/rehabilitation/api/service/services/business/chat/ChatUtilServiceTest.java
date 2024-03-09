package rehabilitation.api.service.services.business.chat;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatUtilService;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ChatUtilServiceTest {
    @InjectMocks
    private ChatUtilService underTheTest;
    @Mock
    private ChatUserRepository chatUserRepository;

    @Test
    void Should_FindChatUser_When_LoginExists() throws NotFoundLoginException {
        // given
        String login = "testUser";
        ChatUser expectedChatUser = new ChatUser();

        // when
        when(chatUserRepository.findChatUserByLogin(login)).thenReturn(Optional.of(expectedChatUser));

        // then
        ChatUser result = underTheTest.findByLogin(login);

        // Assert
        assertThat(result).isEqualTo(expectedChatUser);

        // Verify that the repository method was called with the correct argument
        verify(chatUserRepository).findChatUserByLogin(login);
    }
    @Test
    void Should_ThrowNotFoundLoginException_When_LoginNotExist() {
        // Arrange
        String nonExistentLogin = "nonExistentUser";

        // Mock the behavior of chatUserRepository when login does not exist
        when(chatUserRepository.findChatUserByLogin(nonExistentLogin)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> underTheTest.findByLogin(nonExistentLogin))
                .isInstanceOf(NotFoundLoginException.class);

        // Verify that the repository method was called with the correct argument
        verify(chatUserRepository).findChatUserByLogin(nonExistentLogin);
    }
}
