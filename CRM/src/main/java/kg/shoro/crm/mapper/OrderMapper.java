package kg.shoro.crm.mapper;

import kg.shoro.crm.model.Order;
import kg.spring.shared.dto.response.OrderResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    List<OrderResponse> toResponseList(List<Order> orders);
}
