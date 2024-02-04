package rehabilitation.api.service.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMessage {
    @MongoId
    private String id;
    private String chatId;
    private String senderLogin;
    private String recipientLogin;
    private String content;
    private LocalDate timestamp;
}
