package kg.shoro.payment_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SessionCreateRequest (
    String externalOrderId,
    BigDecimal totalAmount,
    List<BigDecimal> parts,
    Integer partCount,
    Map<String, String> metadata // optional
    )
{}
