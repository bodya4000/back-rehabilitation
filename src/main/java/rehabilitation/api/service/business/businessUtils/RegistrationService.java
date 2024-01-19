package rehabilitation.api.service.business.businessUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.dto.RegistrationDto;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private final ClientRepository clientRepository;
    @Autowired
    private final SpecialistRepository specialistRepository;
    @Autowired
    private final ReHubRepository reHubRepository;

    public ClientModel registerClient(RegistrationDto registrationDto) {
        ClientModel client = createUser(registrationDto, Role.ROLE_CLIENT);
        clientRepository.save(client);
        return client;
    }

    public SpecialistModel registerSpecialist(RegistrationDto registrationDto) {
        SpecialistModel specialist = createUser(registrationDto, Role.ROLE_SPECIALIST);
        specialistRepository.save(specialist);
        return specialist;
    }

    public ReHubModel registerReHub(RegistrationDto registrationDto) {
        ReHubModel reHubModel = createUser(registrationDto, Role.ROLE_REHUB);
        reHubRepository.save(reHubModel);
        return reHubModel;
    }

    private <T extends UserModel> T createUser(RegistrationDto registrationDto, Role role) {
        T user = (T) new UserModel();
        user.setLogin(registrationDto.login());
        user.setEmail(registrationDto.email());
        user.setPassword(passwordEncoder.encode(registrationDto.password()));
        var userRole = new UserRole(role, user);
        user.getRoles().add(userRole);
        return user;
    }
}
