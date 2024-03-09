package rehabilitation.api.service.entity.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class ChatUser {
    @MongoId
    @Field(targetType = FieldType.STRING)
    private String login;
    private String firstName;
    private String lastName;
    private Set<ChatUser> contacts = new HashSet<>();
    private Set<ChatUser> blockedContacts = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUser chatUser = (ChatUser) o;
        return Objects.equals(login, chatUser.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
