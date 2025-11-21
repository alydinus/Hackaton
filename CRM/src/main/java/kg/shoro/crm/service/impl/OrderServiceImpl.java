package kg.shoro.crm.service.impl;

import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.shoro.crm.exception.OrderNotFoundException;
import kg.shoro.crm.model.Order;
import kg.shoro.crm.repository.OrderRepository;
import kg.shoro.crm.service.CustomerService;
import kg.shoro.crm.service.OrderService;
import kg.shoro.crm.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + id)
        );
    }

    public Order createOrder(CreateOrderRequest request) {
        return Order.builder()
                .customer(customerService.getCustomerById(request.customerId()))
                .orderProducts(productService.getProductsByIds(request.productIds()))
                .build();
    }

    public Order updateOrder(Long id, UpdateOrderRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + id)
        );
        order.setCustomer(customerService.getCustomerById(request.customerId()));
        order.setOrderProducts(productService.getProductsByIds(request.productIds()));
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + id)
        );
        orderRepository.delete(order);
    }
}
