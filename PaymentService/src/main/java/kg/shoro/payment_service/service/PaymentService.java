package kg.shoro.payment_service.service;

import kg.shoro.payment_service.dto.SessionCreateRequest;
import kg.shoro.payment_service.dto.SessionResponse;
import kg.shoro.payment_service.model.PaymentSession;
import org.jspecify.annotations.Nullable;

public interface PaymentService {
    PaymentSession createSession(SessionCreateRequest request);

    PaymentSession getSession(Long sessionId);

//    byte[] getQrForSession(Long sessionId);


}
