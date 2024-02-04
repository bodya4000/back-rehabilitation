package rehabilitation.api.service.entity.sql;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ToString(exclude = "clients")
@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "specialists")
@Entity
@Document(indexName = "specialist_index")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecialistModel extends UserModel {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private String searchId;

    @Field(type = FieldType.Keyword)
    private String login;
    @Field(type = FieldType.Text)
    private String firstName;
    @Field(type = FieldType.Text)
    private String lastName;

    private String city;

    private int age;

    @Column
    private int experience;

    @Column
    private int rate;

    @Column
    private String description;

    @Column
    private String speciality;

    @Transient
    @JsonIgnoreProperties({"specialists, client"})
    @ManyToMany(mappedBy = "specialists", fetch = FetchType.LAZY)
    private Set<ClientModel> clients = new HashSet<>();

    @Transient
    @JoinColumn(name = "re_hub_login")
    @JsonIgnoreProperties({"reHub, specialists"})
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ReHubModel reHub;

    public void addClient(ClientModel client) {
        clients.add(client);
        client.getSpecialists().add(this);
    }

    public void removeClient(ClientModel client) {
        clients.remove(client);
        client.getSpecialists().remove(this);
    }

    public List<String> getListOfClientsLogin(){
        return clients.stream().map(UserModel::getLogin).toList();
    }
}
