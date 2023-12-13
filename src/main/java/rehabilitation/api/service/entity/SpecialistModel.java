package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

//@ToString(exclude = "clients")
@ToString
//@EqualsAndHashCode(of = "login")
@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "specialists")
@Entity
public class SpecialistModel {
    @Id
    private String login;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String type;
    @Column
    private String phoneNumber;
    @Column
    private String email;
    @Column
    private int experience;
    @Column
    private int rate;
    @Column
    private String description;
    @Column
    private String imgUrl;

    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties({"specialists"})
    @ManyToMany(mappedBy = "specialists", fetch = FetchType.LAZY)
    private Set<ClientModel> clients = new HashSet<>();

    @JoinColumn(name = "re_hub_login")
    @JsonIgnoreProperties({"specialists"})
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private ReHubModel reHub;

    public void addClient(ClientModel client) {
        clients.add(client);
        client.getSpecialists().add(this);
    }

    public void removeClient(ClientModel client) {
        clients.remove(client);
        client.getSpecialists().remove(this);
    }

}
