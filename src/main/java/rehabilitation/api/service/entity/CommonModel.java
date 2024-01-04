package rehabilitation.api.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public abstract class CommonModel {
    @Id
    private String login;

    @Column
    private String firstName;

    @Column
    private String email;

    @Column
    private String contactInformation;

    @Column
    private String password;

    private Set<UserRole> roles;
}
