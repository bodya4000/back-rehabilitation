package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@ToString(exclude = "specialists")
@Setter
@Getter
@AllArgsConstructor
@Table(name = "clients")
@Entity
public class ClientModel extends CommonModel {
    @Id
    private String login;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String address;

    @Column
    private String contactInformation;

    private String imgUrl;

    private String password;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<UserRole> roles = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties({"clients"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "clients_specialists",
            joinColumns = {
                    @JoinColumn(
                            name = "client_login"
                    )},
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "specialist_login"
                    )})
    private Set<SpecialistModel> specialists = new HashSet<>();

    public ClientModel() {
    }

    public void addSpecialist(SpecialistModel specialist) {
        specialists.add(specialist);
        specialist.getClients().add(this);
    }

    public void removeSpecialist(SpecialistModel specialist) {
        specialists.remove(specialist);
        specialist.getClients().remove(this);
    }

}
