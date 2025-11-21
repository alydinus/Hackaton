package kg.shoro.payment_service.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record SessionResponse (
        Long id,
        String paymentLink,
        String qrUrl
)
{}