package rehabilitation.api.service.dto;

import java.util.List;

public record RehubDto(
        String login,
        String name,
        String location,
        String contactInformation,
        int rating,
        List<String> specialists,
        List<String> clients
) {
}
