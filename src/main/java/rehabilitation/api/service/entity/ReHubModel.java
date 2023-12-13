package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


import java.util.Set;

@ToString(exclude = "specialists")
@EqualsAndHashCode(of = "login")
@Setter@Getter
@Entity
@Table(name = "re_hubs")
public class ReHubModel {

    @Id
    private String login;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "contact_information", nullable = false)
    private String contactInformation;

    private int rating;
//    @ElementCollection
//    @CollectionTable(name = "services", joinColumns = @JoinColumn(name = "rehub_id"))
//    @Column(name = "service")
//    private List<String> services;

    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties("reHub")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reHub")
    private Set<SpecialistModel> specialists;

    public void addSpecialist(SpecialistModel specialist) {
        specialists.add(specialist);
        specialist.setReHub(this);
    }

    public void removeSpecialist(SpecialistModel specialist) {
        specialists.remove(specialist);
        specialist.setReHub(null);
    }
}
