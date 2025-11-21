package kg.shoro.crm.dto.request;

import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<Long> productIds
) {
}
