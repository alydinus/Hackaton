package kg.spring.shared.dto.response;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        Double totalPrice
) {
}
