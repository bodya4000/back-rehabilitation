package rehabilitation.api.service.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ChatRoom {
    @MongoId
    private String id;
    private String chatId;
    private String senderLogin;
    private String receiverLogin;
}
