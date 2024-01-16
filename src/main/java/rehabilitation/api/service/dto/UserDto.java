package rehabilitation.api.service.dto;

public record UserDto (
        String login,

        String email,
        String contactInformation,

        String address,

        String imgUrl
    ) {
}
