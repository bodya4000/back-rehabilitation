package rehabilitation.api.service.dto.auth;

public record JwtResponseDto(
        String accessToken,
        String refreshToken
) {
}
