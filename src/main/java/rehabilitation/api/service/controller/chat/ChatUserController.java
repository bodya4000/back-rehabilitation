package rehabilitation.api.service.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.businessServices.chatBusiness.ChatUserService;
import rehabilitation.api.service.dto.chat.ChatUserDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

import java.util.Set;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatUserService chatUserService;

    @GetMapping("/contacts")
    private Set<ChatUser> getAllUsersContacts(@RequestBody ChatUserDto chatUserDto) throws NotFoundLoginException {
        return chatUserService.getAllContacts(chatUserDto);
    }

    @MessageMapping("/user.block")
    public void blockContact(
            @Payload ChatUserDto userDto,
            @Payload ChatUserDto contactDto
    ) throws NotFoundLoginException {
        chatUserService.blockContact(userDto, contactDto);
    }

    @MessageMapping("/user.unblock")
    public void unblockContact(
            @Payload ChatUserDto userDto,
            @Payload ChatUserDto contactDto
    ) throws NotFoundLoginException {
        chatUserService.unblockContact(userDto, contactDto);
    }

    private final ChatUserRepository chatUserRepository;
    @PostMapping("/chat/user")
    public void createTestChatUser(
            @RequestBody ChatUserDto chatUserDto
    ) {
        var user = new ChatUser();
        user.setLogin(chatUserDto.login());
        user.setFirstName("firstName");
        user.setLastName("lastName");
        chatUserRepository.save(user);
    }
}
