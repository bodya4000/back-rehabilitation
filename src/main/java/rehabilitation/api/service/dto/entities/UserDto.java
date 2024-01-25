package rehabilitation.api.service.dto.entities;

public record UserDto (
        String login,

        String email,
        String contactInformation,

        String address,

        String imgUrl
    ) {
}
