package kg.shoro.payment_service.service.impl;

import jakarta.transaction.Transactional;
import kg.shoro.payment_service.dto.PartDto;
import kg.shoro.payment_service.dto.SessionCreateRequest;
import kg.shoro.payment_service.dto.SessionResponse;
import kg.shoro.payment_service.mapper.PaymentPartMapper;
import kg.shoro.payment_service.mapper.PaymentSessionMapper;
import kg.shoro.payment_service.model.PaymentPart;
import kg.shoro.payment_service.model.PaymentSession;
import kg.shoro.payment_service.enums.PartStatus;
import kg.shoro.payment_service.enums.PaymentSessionStatus;
import kg.shoro.payment_service.exception.NotFoundException;
import kg.shoro.payment_service.repository.PaymentPartRepository;
import kg.shoro.payment_service.repository.PaymentSessionRepository;
import kg.shoro.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentSessionRepository sessionRepository;
    private final PaymentPartRepository partRepository;
    private final PaymentSessionMapper paymentSessionMapper;
    private final PaymentPartMapper paymentPartMapper;

    // ---- GET SESSION ----
    @Transactional
    public SessionResponse getSession(Long sessionId) {
        PaymentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
        // PaymentMapper already maps nested parts via PartMapper
        return paymentSessionMapper.toResponse(session);
    }

    // ---- CREATE SESSION ----
    @Transactional
    public SessionResponse createSession(SessionCreateRequest request) {
        PaymentSession paymentSession = PaymentSession.builder()
                .externalOrderId(request.externalOrderId())
                .totalAmount(request.totalAmount())
                .status(PaymentSessionStatus.CREATED)
                .createdAt(Instant.now())
                .build();

        List<PaymentPart> parts = buildParts(paymentSession, request);
        paymentSession.setParts(parts);

        sessionRepository.save(paymentSession);
        partRepository.saveAll(parts);

        return paymentSessionMapper.toResponse(paymentSession); // maps parts automatically
    }

    // ---- GET SINGLE PAYMENT PART ----
    @Transactional
    public PartDto getPartByLinkId(String linkId) {
        PaymentPart part = partRepository.findByPaymentLinkId(linkId)
                .orElseThrow(() -> new NotFoundException("Payment part not found: " + linkId));
        return paymentPartMapper.toResponse(part); // use PaymentMapper only if you want single-part mapping
    }

    // ---- PAY PART ----
    @Transactional
    public PartDto payPart(String linkId) {
        PaymentPart part = partRepository.findByPaymentLinkId(linkId)
                .orElseThrow(() -> new NotFoundException("Payment part not found: " + linkId));

        if (part.getStatus() == PartStatus.PAID) {
            return paymentPartMapper.toResponse(part); // idempotent
        }

        part.setStatus(PartStatus.PAID);
        part.setPaidAt(Instant.now());
        partRepository.save(part);

        PaymentSession session = part.getSession();
        boolean allPaid = session.getParts().stream()
                .allMatch(p -> p.getStatus() == PartStatus.PAID);
        session.setStatus(allPaid ? PaymentSessionStatus.PAID : PaymentSessionStatus.PARTIALLY_PAID);
        sessionRepository.save(session);

        // TODO: publish RabbitMQ event here if needed
        return paymentPartMapper.toResponse(part);
    }

    // ---- HELPERS ----
    private PaymentPart buildPart(PaymentSession session, BigDecimal amount) {
        return PaymentPart.builder()
                .session(session)
                .amount(amount)
                .paymentLinkId(UUID.randomUUID().toString())
                .status(PartStatus.NOT_PAID)
                .build();
    }

    private List<PaymentPart> buildParts(PaymentSession session, SessionCreateRequest request) {
        if (request.parts() != null && !request.parts().isEmpty()) {
            return request.parts().stream()
                    .map(amount -> buildPart(session, amount))
                    .toList();
        } else if (request.partCount() != null && request.partCount() > 0) {
            BigDecimal partAmount = request.totalAmount()
                    .divide(BigDecimal.valueOf(request.partCount()), 2, RoundingMode.HALF_UP);
            return IntStream.range(0, request.partCount())
                    .mapToObj(i -> buildPart(session, partAmount))
                    .toList();
        } else {
            return List.of(buildPart(session, request.totalAmount()));
        }
    }
}
