package kg.shoro.payment_service.repository;

import kg.shoro.payment_service.model.PaymentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentSessionRepository extends JpaRepository<PaymentSession, Long> {
    Optional<PaymentSession> findByExternalOrderId(String externalOrderId);
}
