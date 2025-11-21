package kg.shoro.payment_service.service;

import kg.shoro.payment_service.dto.PartDto;
import kg.shoro.payment_service.dto.SessionCreateRequest;
import kg.shoro.payment_service.dto.SessionResponse;
import org.jspecify.annotations.Nullable;

public interface PaymentService {
    SessionResponse createSession(SessionCreateRequest request);

    @Nullable SessionResponse getSession(Long sessionId);

    @Nullable PartDto getPartByLinkId(String linkId);

    @Nullable PartDto payPart(String linkId);
}
