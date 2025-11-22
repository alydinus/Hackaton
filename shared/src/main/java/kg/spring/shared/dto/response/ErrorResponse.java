package kg.spring.shared.dto.response;

import java.time.Instant;

public record ErrorResponse (
        String message,
        Integer statusCode,
        String status,
        Instant timestamp
){
}
