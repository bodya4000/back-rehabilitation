package rehabilitation.api.service.dto.auth;

import rehabilitation.api.service.entity.Role;
import rehabilitation.api.service.entity.UserType;

public record RegistrationDto (
        String login,
        String email,
        String password,
        String confirmedPassword,
        UserType userType
) {
}
