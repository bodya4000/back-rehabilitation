package rehabilitation.api.service.dto;

public record RegistrationDto (
        String login,
        String email,
        String password,
        String confirmedPassword
) {
}
