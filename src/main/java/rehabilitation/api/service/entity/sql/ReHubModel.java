package rehabilitation.api.service.entity.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ToString(exclude = "specialists")
@Setter@Getter
@Entity
@Table(name = "re_hubs")
public class ReHubModel extends UserModel {
    private String name;

    private String city;

    private int rating;
//    @ElementCollection
//    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "rehub_login"))
//    private List<Role> roles = new ArrayList<>();

    @JsonIgnoreProperties({"specialists, reHub"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reHub", cascade = CascadeType.MERGE)
    private Set<SpecialistModel> specialists = new HashSet<>();

    public void addSpecialist(SpecialistModel specialist) {
        specialists.add(specialist);
        specialist.setReHub(this);
    }

    public void removeSpecialist(SpecialistModel specialist) {
        specialists.remove(specialist);
        specialist.setReHub(null);
    }

    public List<String> getListOfSpecialistsLogin(){
        return specialists.stream().map(SpecialistModel::getLogin).toList();
    }
}
