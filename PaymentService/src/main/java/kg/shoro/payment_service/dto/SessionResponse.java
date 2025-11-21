package kg.shoro.payment_service.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record SessionResponse (
    Long sessionId,
    String externalOrderId,
    BigDecimal totalAmount,
    String status,
    Instant createdAt,
    List<PartDto> parts
    )
{}