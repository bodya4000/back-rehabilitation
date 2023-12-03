package veres.lection.first.rest.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "specialists")
@Entity
public class SpecialistModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String login;
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

    @ManyToMany(mappedBy = "specialists", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIgnoreProperties({"specialists"})
    private Set<ClientModel> clients = new HashSet<>();

    @JoinColumn(name = "re_hub")
    @ManyToOne(fetch = FetchType.EAGER)
    private ReHubModel reHub;

    public void addClient(ClientModel client) {
        clients.add(client);
        client.getSpecialists().add(this);
    }

}
