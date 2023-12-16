package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@ToString(exclude = "specialists")
@EqualsAndHashCode(of = "login")
@Setter
@Getter
@AllArgsConstructor
@Table(name = "clients")
@Entity
public class ClientModel {
    @Id
    private String login;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String address;
    @Column
    private String email;
    @Column
    private String phoneNumber;
    @Column
    private String healthState;

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
