package kg.shoro.payment_service.service.impl;

import jakarta.transaction.Transactional;
import kg.shoro.payment_service.dto.SessionCreateRequest;
import kg.shoro.payment_service.model.PaymentSession;
import kg.shoro.payment_service.exception.NotFoundException;
import kg.shoro.payment_service.repository.PaymentSessionRepository;
import kg.shoro.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentSessionRepository paymentSessionRepository;

    public PaymentSession getSession(Long sessionId) {
        return paymentSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
    }

    @Transactional
    public PaymentSession createSession(SessionCreateRequest request) {

        PaymentSession session = new PaymentSession();
        session.setTotalAmount(request.totalAmount());
        paymentSessionRepository.save(session);

        String paymentLink =
                "https://localhost:8080/api/v1/pay/" + session.getId();

        String qrUrl =
                "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" +
                        URLEncoder.encode(paymentLink, StandardCharsets.UTF_8);

        session.setPaymentLink(paymentLink);
        session.setQrUrl(qrUrl);

        return paymentSessionRepository.save(session);
    }

//    @Override
//    public byte[] getQrForSession(Long sessionId) {
//        PaymentSession session = paymentSessionRepository.findById(sessionId)
//                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
//
//        String qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="
//                + "http://localhost:8080/pay/" + session.getId();
//
//        // Fetch QR image as byte[]
//        RestTemplate restTemplate = new RestTemplate();
//        byte[] qrImage = restTemplate.getForObject(qrApiUrl, byte[].class);
//
//        if (qrImage == null || qrImage.length == 0) {
//            throw new RuntimeException("Failed to fetch QR code");
//        }
//
//        return qrImage;
//    }

//    @Transactional
//    public SessionResponse paySession(Long sessionId) {
//        PaymentSession session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
//
//        session.setStatus(PaymentSessionStatus.PAID);
//        sessionRepository.save(session);
//
//        return paymentSessionMapper.toResponse(session);
//    }


}
