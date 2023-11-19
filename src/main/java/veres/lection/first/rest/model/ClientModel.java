package veres.lection.first.rest.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Table(name = "clients")
@Entity
public class ClientModel implements Model{
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialist_id")
    @JsonIgnoreProperties({"specialist_id", "clients"})
    private SpecialistModel specialistModel;

    public ClientModel() {
        this.firstName = "default";
        this.lastName = "default";
        this.login = "default" + id;
        this.address = "default";
        this.email = "default";
        this.phoneNumber = "0997622482";
        this.healthState = "default";
    }
}
