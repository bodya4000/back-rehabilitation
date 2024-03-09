package rehabilitation.api.service.entity.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder
@EqualsAndHashCode(of = {"id"})
public class ChatRoom {
    @MongoId
    private String id;
    private String chatId;
    private String senderLogin;
    private String receiverLogin;
}
