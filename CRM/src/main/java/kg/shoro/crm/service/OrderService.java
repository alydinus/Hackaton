package kg.shoro.crm.service;

import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.shoro.crm.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(CreateOrderRequest request);
    Order updateOrder(Long id, UpdateOrderRequest request);
    void deleteOrder(Long id);

    void attachQrToOrder(Long aLong, String s);

    Long countOrdersInLastDays(Long days);

    Double calculateTotalRevenueInLastDays(Long days);
}
