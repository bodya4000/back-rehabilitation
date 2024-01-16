package rehabilitation.api.service.dto;

public record SearchDto(
        String city, String age, String speciality, String searchText
) {
}
