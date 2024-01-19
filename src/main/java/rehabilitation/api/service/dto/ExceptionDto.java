package rehabilitation.api.service.dto;

import org.springframework.http.HttpStatus;

import java.util.Date;

public record ExceptionDto (
        String message,
        Date timestamp
) {
}
