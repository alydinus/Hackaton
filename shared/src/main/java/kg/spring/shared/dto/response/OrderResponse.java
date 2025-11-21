package kg.spring.shared.dto.response;

import java.util.List;

public record OrderResponse(
        Long id,
        CustomerResponse customer,
        List<ProductResponse> products
) {
}
