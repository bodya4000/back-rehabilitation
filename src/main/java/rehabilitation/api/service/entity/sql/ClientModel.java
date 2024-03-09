package rehabilitation.api.service.entity.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@Table(name = "clients")
@Entity
public class ClientModel extends UserModel {
    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties({"clients"})
    @ManyToMany(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinTable(
            name = "clients_specialists",
            joinColumns = {
                    @JoinColumn(
                            name = "client_login"
                    )},
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "specialist_login"
                    )})
    private Set<SpecialistModel> specialists = new HashSet<>();

    public ClientModel() {
    }

    public void addSpecialist(SpecialistModel specialist) {
        specialists.add(specialist);
        specialist.getClients().add(this);
    }

    public void removeSpecialist(SpecialistModel specialist) {
        specialists.remove(specialist);
        specialist.getClients().remove(this);
    }

    public List<String> getListOfSpecialistsLogin(){
        return specialists.stream().map(SpecialistModel::getLogin).toList();
    }
}
