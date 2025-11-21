package kg.shoro.payment_service.repository;

import kg.shoro.payment_service.model.PaymentPart;
import kg.shoro.payment_service.enums.PartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentPartRepository extends JpaRepository<PaymentPart, Long> {
    Optional<PaymentPart> findByPaymentLinkId(String linkId);
    int countBySessionIdAndStatusNot(Long sessionId, PartStatus status);
}
