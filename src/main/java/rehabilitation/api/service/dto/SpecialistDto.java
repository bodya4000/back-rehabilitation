package rehabilitation.api.service.dto;

import java.util.List;
import java.util.Set;

public record SpecialistDto (
        String login,
        String firstName,
        String lastName,
        String city,
        int age,
        int experience,
        int rate,
        String type,
        String imgUrl,
        String description,
        String rehub,
        List<String> clientLogin
        ){

}
