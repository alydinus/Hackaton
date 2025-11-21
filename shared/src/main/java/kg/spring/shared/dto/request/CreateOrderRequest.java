package kg.spring.shared.dto.request;

import kg.spring.shared.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<Long> productIds,
        Integer quantity,
        OrderStatus orderStatus,
        LocalDateTime createdAt,
        Double price
) {
}
