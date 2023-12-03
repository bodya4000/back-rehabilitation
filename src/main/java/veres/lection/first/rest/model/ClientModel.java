package veres.lection.first.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@ToString
@AllArgsConstructor
@Table(name = "clients")
@Entity
public class ClientModel {
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
    private String address;
    @Column
    private String email;
    @Column
    private String phoneNumber;
    @Column
    private String healthState;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "clients_specialists",
            joinColumns = {
                    @JoinColumn(
                            name = "client_id"
                    )},
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "specialist_id"
                    )})
    @JsonIgnoreProperties({"clients"})
    private Set<SpecialistModel> specialists;

    public ClientModel() {
        this.specialists = new HashSet<>();
    }

    public void addSpecialist(SpecialistModel specialist) {
        specialists.add(specialist);
        specialist.getClients().add(this);
    }
}
