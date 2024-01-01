package rehabilitation.api.service.entity;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseModel {
    @Id
    private String login;

    @Column
    private String firstName;

    @Column
    private String email;

    @Column
    private String contactInformation;

}
