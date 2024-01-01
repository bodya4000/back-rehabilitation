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
public class SpecialistModel extends BaseModel {
    @Id
    private String login;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contactInformation;

    @Column
    private int experience;

    @Column
    private int rate;

    @Column
    private String description;

    private String imgUrl;

    @Column
    private String type;

    @JsonIgnoreProperties({"specialists"})
    @ManyToMany(mappedBy = "specialists", fetch = FetchType.LAZY)
    private Set<ClientModel> clients = new HashSet<>();

    @JoinColumn(name = "re_hub_login")
    @JsonIgnoreProperties({"reHub, specialists"})
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
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
