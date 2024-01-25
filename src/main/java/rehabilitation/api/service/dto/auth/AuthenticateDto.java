package rehabilitation.api.service.dto.auth;

public record AuthenticateDto(
        String login,
        String password
) {
}
