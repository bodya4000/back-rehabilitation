package rehabilitation.api.service.business.businessServices.securityBusiness.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.securityBusiness.RegistrationService;
import rehabilitation.api.service.business.businessServices.securityBusiness.ValidateService;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.exceptionHandling.exception.auth.BadRequestException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;

import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final ValidateService validateService;
    private final RegistrationService registrationService;
    public void signUp(RegistrationDto registrationDto) throws BadRequestException, AlreadyExistLoginException {
        performValidation(registrationDto);
        registerUserByType(registrationDto);
    }

    private void performValidation(RegistrationDto registrationDto) throws AlreadyExistLoginException {
        validateService.validateRegistration(registrationDto);
    }

    private void registerUserByType(RegistrationDto registrationDto) throws BadRequestException {
        Map<UserType, Consumer<RegistrationDto>> registrationMethods = Map.of(
                UserType.CLIENT, registrationService::registerClient,
                UserType.SPECIALIST, registrationService::registerSpecialist,
                UserType.REHUB, registrationService::registerReHub,
                UserType.ADMIN, registrationService::registerAdmin
        );

        registrationMethods.getOrDefault(registrationDto.userType(), this::throwBadRequestException)
                .accept(registrationDto);
    }

    private void throwBadRequestException(RegistrationDto registrationDto) {
        throw new BadRequestException("Type of user " + registrationDto.userType() + " is incorrect");
    }

}
