package rehabilitation.api.service.dto.chat;

public record ChatMessageDto (
        String senderLogin,
        String recipientLogin,
        String content,
        String chatId
) {
}
