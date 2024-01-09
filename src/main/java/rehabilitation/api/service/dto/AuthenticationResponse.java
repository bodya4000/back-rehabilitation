package rehabilitation.api.service.dto;

import rehabilitation.api.service.entity.UserModel;

public record AuthenticationResponse (
    JwtResponse jwt,
    UserDto user
) { }
