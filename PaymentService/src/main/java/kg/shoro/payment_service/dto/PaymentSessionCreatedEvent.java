package kg.shoro.payment_service.dto;

import lombok.Data;

@Data
public class PaymentSessionCreatedEvent {
    private Long sessionId;
    private String paymentLink;
}