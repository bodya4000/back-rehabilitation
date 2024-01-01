package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


import java.util.Set;

@ToString(exclude = "specialists")
@Setter@Getter
@Entity
@Table(name = "re_hubs")
public class ReHubModel extends BaseModel{

    @Id
    private String login;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contactInformation;

    private String imgUrl;

    private int rating;

    @JsonIgnoreProperties({"specialists, reHub"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reHub", cascade = CascadeType.MERGE)
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
