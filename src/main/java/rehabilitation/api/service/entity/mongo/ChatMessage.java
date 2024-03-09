package rehabilitation.api.service.entity.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
@EqualsAndHashCode(of = {"id", "content", "chatId", "senderLogin", "recipientLogin"})
public class ChatMessage {
    @MongoId
    private String id;
    private String chatId;
    private String senderLogin;
    private String recipientLogin;
    private String content;
    private LocalDate timestamp;

}
