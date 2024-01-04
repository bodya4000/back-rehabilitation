package rehabilitation.api.service.dto;

import java.util.Date;

public record ExceptionDto (
        String content,
        Object status,

        Date timestamp
) {
}
