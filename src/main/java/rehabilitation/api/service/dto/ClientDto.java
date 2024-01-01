package rehabilitation.api.service.dto;

import java.util.List;

public record ClientDto (
        String login,
        String firstName,
        String lastName,
        String email,
        String address,
        String contactInformation,
        String imgUrl,
        List<String> specialistLogin
){
}
