package kg.spring.shared.dto.response;

public class OrderCreatedEvent {

    private Long orderId;
    private Long customerId;
    private Double totalPrice;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(Long orderId, Long customerId, Double totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }
}
