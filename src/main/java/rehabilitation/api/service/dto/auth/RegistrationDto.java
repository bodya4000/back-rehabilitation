package rehabilitation.api.service.dto.auth;

import rehabilitation.api.service.entity.sql.UserType;

public record RegistrationDto (
        String login,
        String email,
        String password,
        UserType userType
) {
}
