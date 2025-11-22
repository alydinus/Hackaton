package kg.spring.shared.dto.response;

import kg.spring.shared.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        CustomerResponse customer,
        List<ProductResponse> products,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
