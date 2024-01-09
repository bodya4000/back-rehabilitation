package rehabilitation.api.service.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import rehabilitation.api.service.entity.Role;
import rehabilitation.api.service.entity.UserRole;

import java.util.HashSet;
import java.util.Set;

public record UserDto (
        String login,

        String email,

        String firstName,

        String lastName,

        String contactInformation,

        String address,

        String imgUrl
    ) {
}
