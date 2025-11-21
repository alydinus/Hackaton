package kg.spring.shared.dto.request;

import java.util.List;

public record UpdateOrderRequest(
        Long customerId,
        List<Long> productIds
) {
}
