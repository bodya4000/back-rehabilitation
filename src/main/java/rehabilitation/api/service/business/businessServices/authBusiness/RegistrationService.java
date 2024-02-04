package rehabilitation.api.service.business.businessServices.authBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessUtils.ESUtils;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.mongo.ChatUser;
import rehabilitation.api.service.entity.sql.*;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.AdminRepository;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.elasticsearch.SpecialistSearchRepository;
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    // todo regroup this service on smaller pieces of logic

    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final SpecialistRepository specialistRepository;
    private final SpecialistSearchRepository specialistSearchRepository;
    private final ReHubRepository reHubRepository;
    private final ChatUserRepository chatUserRepository;
    private final AdminRepository adminRepository;

    public void registerAdmin(RegistrationDto registrationDto) {
        var admin = new AdminModel();
        admin.setLogin(registrationDto.login());
        admin.setPassword(passwordEncoder.encode(registrationDto.password()));
        adminRepository.save(admin);
    }

    @Transactional
    public void registerClient(RegistrationDto registrationDto) {
        ClientModel client = createClient(registrationDto);
        clientRepository.save(client);
        saveClientToChatUsers(client);
    }

    @Transactional
    public void registerSpecialist(RegistrationDto registrationDto) {
        SpecialistModel specialist = createSpecialist(registrationDto);
        specialistRepository.save(specialist);
        saveSpecialistToChatUsers(specialist);
        ESUtils.mapForES(SpecialistModel.class,specialist).map(specialistSearchRepository::save);
    }

    @Transactional
    public void registerReHub(RegistrationDto registrationDto) {
        ReHubModel reHubModel = createReHub(registrationDto);
        reHubRepository.save(reHubModel);
    }

    private ClientModel createClient(RegistrationDto registrationDto) {
        var client = new ClientModel();
        client.setLogin(registrationDto.login());
        client.setEmail(registrationDto.email());
        client.setPassword(passwordEncoder.encode(registrationDto.password()));
        var userRole = new UserRole(Role.ROLE_CLIENT, client);
        client.getRoles().add(userRole);
        return client;
    }

    private SpecialistModel createSpecialist(RegistrationDto registrationDto) {
        var specialist = new SpecialistModel();
        specialist.setLogin(registrationDto.login());
        specialist.setEmail(registrationDto.email());
        specialist.setPassword(passwordEncoder.encode(registrationDto.password()));
        var userRole = new UserRole(Role.ROLE_SPECIALIST, specialist);
        specialist.getRoles().add(userRole);
        return specialist;
    }

    private ReHubModel createReHub(RegistrationDto registrationDto) {
        var reHub = new ReHubModel();
        reHub.setLogin(registrationDto.login());
        reHub.setEmail(registrationDto.email());
        reHub.setPassword(passwordEncoder.encode(registrationDto.password()));
        var userRole = new UserRole(Role.ROLE_REHUB, reHub);
        reHub.getRoles().add(userRole);
        return reHub;
    }

    private void saveClientToChatUsers(ClientModel client){
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(client.getLogin());
        chatUser.setFirstName(client.getFirstName());
        chatUser.setLastName(client.getLastName());
        chatUserRepository.save(chatUser);
    }

    private void saveSpecialistToChatUsers(SpecialistModel specialist){
        ChatUser chatUser = new ChatUser();
        chatUser.setLogin(specialist.getLogin());
        chatUser.setFirstName(specialist.getFirstName());
        chatUser.setLastName(specialist.getLastName());
        chatUserRepository.save(chatUser);
    }

}
