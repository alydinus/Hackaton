package kg.spring.shared.dto.request;

import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<Long> productIds
) {
}
