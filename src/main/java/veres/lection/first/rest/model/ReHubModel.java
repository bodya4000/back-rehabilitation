package veres.lection.first.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;

@Setter@Getter
@Entity
@Table(name = "re_hubs")
public class ReHubModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String location;

    @Column(name = "contact_information")
    private String contactInformation;

//    @ElementCollection
//    @CollectionTable(name = "services", joinColumns = @JoinColumn(name = "rehub_id"))
//    @Column(name = "service")
//    private List<String> services;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "reHub")
    //@JsonIgnoreProperties("specialists")
    @JsonIgnore
    private Set<SpecialistModel> specialists;

    private int rating;

}
