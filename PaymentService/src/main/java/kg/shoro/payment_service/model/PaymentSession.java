package kg.shoro.payment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import kg.shoro.payment_service.enums.PaymentSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSession {
    @Id
    @GeneratedValue
    private Long id;

    private String externalOrderId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentSessionStatus status;

    private Instant createdAt;

    private String paymentLink;

    private String qrUrl;

}
