package rehabilitation.api.service.business.businessServices.authBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessUtils.ESUtils;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.elasticsearch.SpecialistSearchRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final SpecialistRepository specialistRepository;
    private final SpecialistSearchRepository specialistSearchRepository;
    private final ReHubRepository reHubRepository;

    public ClientModel registerClient(RegistrationDto registrationDto) {
        ClientModel client = createClient(registrationDto);
        clientRepository.save(client);
        return client;
    }

    public SpecialistModel registerSpecialist(RegistrationDto registrationDto) {
        SpecialistModel specialist = createSpecialist(registrationDto);
        specialistRepository.save(specialist);
        ESUtils.mapForES(SpecialistModel.class,specialist).map(specialistSearchRepository::save);
        return specialist;
    }

    public ReHubModel registerReHub(RegistrationDto registrationDto) {
        ReHubModel reHubModel = createReHub(registrationDto);
        reHubRepository.save(reHubModel);
        return reHubModel;
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


}
