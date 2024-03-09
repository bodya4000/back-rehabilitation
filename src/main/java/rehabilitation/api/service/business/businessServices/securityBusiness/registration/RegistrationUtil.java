package rehabilitation.api.service.business.businessServices.securityBusiness.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;

@Component
@RequiredArgsConstructor
public class RegistrationUtil {

    private final PasswordEncoder passwordEncoder;

    public <T extends UserModel> T create(
            RegistrationDto registrationDto, Role role, Class<T> userModelClass) {
        T userModel;
        try {
            userModel = userModelClass.getDeclaredConstructor().newInstance();
            userModel.setLogin(registrationDto.login());
            userModel.setEmail(registrationDto.email());
            userModel.setPassword(passwordEncoder.encode(registrationDto.password()));
            var userRole = new UserRole(role, userModel);
            userModel.getRoles().add(userRole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user model", e);
        }
        return userModel;
    }
}
