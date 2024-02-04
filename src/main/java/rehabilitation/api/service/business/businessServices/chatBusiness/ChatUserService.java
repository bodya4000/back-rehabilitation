package rehabilitation.api.service.business.businessServices.chatBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.chat.ChatUserDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChatUserService {

    private final ChatUtilService chatUtilService;

    @Transactional
    public void addUserContact(ChatUser user, ChatUser contact){
        user.getContacts().add(contact);
        contact.getContacts().add(user);
    }

    @Transactional
    public void addUserContact(String userLogin, String contactLogin) throws NotFoundLoginException {
        var user = chatUtilService.findByLogin(userLogin);
        var contact = chatUtilService.findByLogin(contactLogin);
        addUserContact(user, contact);
    }

    public Set<ChatUser> getAllContacts(ChatUserDto userDto) throws NotFoundLoginException {
        var user = chatUtilService.findByLogin(userDto.login());
        return user.getContacts();
    }

    public void blockContact(ChatUserDto userDto, ChatUserDto contactDto) throws NotFoundLoginException {
        var user = chatUtilService.findByLogin(userDto.login());
        var contact = chatUtilService.findByLogin(contactDto.login());
        user.getBlockedContacts().add(contact);
    }

    public void unblockContact(ChatUserDto userDto, ChatUserDto contactDto) throws NotFoundLoginException {
        var user = chatUtilService.findByLogin(userDto.login());
        var contact = chatUtilService.findByLogin(contactDto.login());
        user.getBlockedContacts().remove(contact);
    }

}
