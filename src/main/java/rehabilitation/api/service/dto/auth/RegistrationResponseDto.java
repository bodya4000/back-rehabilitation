package rehabilitation.api.service.dto.auth;

public record RegistrationResponseDto (
        String login,
        String firstName,
        String lastName,
        String email
        ) {
}
