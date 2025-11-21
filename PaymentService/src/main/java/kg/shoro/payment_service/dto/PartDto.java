package kg.shoro.payment_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PartDto (
    Long partId,
    BigDecimal amount,
    String linkId,
    String payUrl,
    String qrBase64,
    String status,
    Instant paidAt
    )
{}
