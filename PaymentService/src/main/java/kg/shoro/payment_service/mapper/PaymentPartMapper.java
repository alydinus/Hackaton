package kg.shoro.payment_service.mapper;

import kg.shoro.payment_service.dto.PartDto;
import kg.shoro.payment_service.model.PaymentPart;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentPartMapper {
    PartDto toResponse(PaymentPart part);

    List<PartDto> toResponseList(List<PaymentPart> parts);
}
