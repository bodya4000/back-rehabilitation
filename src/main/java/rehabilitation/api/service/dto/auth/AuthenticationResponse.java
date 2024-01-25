package rehabilitation.api.service.dto.auth;

import rehabilitation.api.service.dto.entities.UserDto;

public record AuthenticationResponse (
    JwtResponse jwt,
    UserDto user
) { }
