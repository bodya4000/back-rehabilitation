package rehabilitation.api.service.dto.chat;

import java.util.Date;
import java.util.List;

public record ChatMessageDtoWithImg(
        String senderLogin,
        String receiver,
        String content,
        List<String> images,
        Date timestamp
) {
}
