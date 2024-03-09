package rehabilitation.api.service.business.businessServices.securityBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.AdminRegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.ClientRegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.ReHubRegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.registration.SpecialistRegistrationService;
import rehabilitation.api.service.dto.auth.RegistrationDto;
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AdminRegistrationService adminRegistrationService;
    private final ClientRegistrationService clientRegistrationService;
    private final SpecialistRegistrationService specialistRegistrationService;
    private final ReHubRegistrationService reHubRegistrationService;

    public void registerAdmin(RegistrationDto registrationDto) {
        adminRegistrationService.register(registrationDto);
    }

    public void registerClient(RegistrationDto registrationDto) {
        clientRegistrationService.register(registrationDto);
    }

    public void registerSpecialist(RegistrationDto registrationDto) {
        specialistRegistrationService.register(registrationDto);
    }

    public void registerReHub(RegistrationDto registrationDto) {
        reHubRegistrationService.register(registrationDto);
    }

}
