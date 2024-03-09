package rehabilitation.api.service.business.businessServices.securityBusiness.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService implements Registration {
    private final ClientRepository clientRepository;
    private final ChatUserRepository chatUserRepository;
    private final RegistrationUtil registrationUtil;

    @Transactional
    @Override
    public void register(RegistrationDto registrationDto) {
        ClientModel client = createClient(registrationDto);
        saveToPostgresDataBase(client);
        saveToMongoDB(client);
    }

    private void saveToPostgresDataBase(ClientModel client) {
        clientRepository.save(client);
    }

    private ClientModel createClient(RegistrationDto registrationDto) {
        return registrationUtil.create(registrationDto, Role.ROLE_CLIENT, ClientModel.class);
    }

    private void saveToMongoDB(ClientModel client){
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(client.getLogin());
        chatUser.setFirstName(client.getFirstName());
        chatUser.setLastName(client.getLastName());
        chatUserRepository.save(chatUser);
    }
}
