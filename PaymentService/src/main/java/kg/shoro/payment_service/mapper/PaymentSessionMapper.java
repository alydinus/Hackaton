package kg.shoro.payment_service.mapper;

import kg.shoro.payment_service.dto.SessionResponse;
import kg.shoro.payment_service.model.PaymentSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentSessionMapper {

    SessionResponse toResponse(PaymentSession session);
}
