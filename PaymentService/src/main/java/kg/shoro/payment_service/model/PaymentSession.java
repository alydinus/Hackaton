package kg.shoro.payment_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import kg.shoro.payment_service.enums.PaymentSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
    private String externalOrderId; // id из CRM

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentSessionStatus status; // CREATED, PARTIALLY_PAID, PAID, CANCELED

    private Instant createdAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentPart> parts;
}
