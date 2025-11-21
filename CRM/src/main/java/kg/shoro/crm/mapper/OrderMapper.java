package kg.shoro.crm.mapper;

import kg.shoro.crm.model.Order;
import kg.spring.shared.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ProductMapper.class})
public interface OrderMapper {
    @Mapping(target = "products", source = "order.products")
    OrderResponse toResponse(Order order);
    List<OrderResponse> toResponseList(List<Order> orders);
}
