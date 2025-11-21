package kg.aiu.telegram_sevrice.service;

import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.spring.shared.dto.response.OrderResponse;

import java.util.List;

public interface OrderServiceClient {
    List<OrderResponse> getAllOrderResponses();
    OrderResponse getOrderResponseById(Long id);
    OrderResponse updateOrder(Long id, UpdateOrderRequest request);
    OrderResponse createOrder(CreateOrderRequest request);
    void deleteOrder(Long id);


}
