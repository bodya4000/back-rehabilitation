package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@ToString(exclude = "clients")
@ToString
//@EqualsAndHashCode(of = "login")
@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "specialists")
@Entity
public class SpecialistModel extends UserModel {

    @Column
    private int experience;

    @Column
    private int rate;

    @Column
    private String description;

    @Column
    private String type;

//    @OneToMany(mappedBy = "login", cascade = CascadeType.ALL)
//    private Set<UserRole> roles = new HashSet<>();

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
