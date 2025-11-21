package kg.shoro.payment_service.mapper;
import kg.shoro.payment_service.dto.SessionResponse;
import kg.shoro.payment_service.model.PaymentSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentPartMapper.class)
public interface PaymentSessionMapper {
    @Mapping(target = "parts", defaultExpression = "java(java.util.Collections.emptyList())")
    SessionResponse toResponse(PaymentSession session);
}


