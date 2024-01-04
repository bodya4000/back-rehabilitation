package rehabilitation.api.service.dto;

public record AuthenticateDto(
        String login,
        String password
) {
}
