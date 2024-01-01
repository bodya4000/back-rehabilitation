package rehabilitation.api.service.dto;

import jakarta.persistence.Column;

import java.util.List;

public record RehubDto(String login, String name, String email,
                       String address, String contactInformation,
                       String imgUrl, List<String> specialists) {
}
