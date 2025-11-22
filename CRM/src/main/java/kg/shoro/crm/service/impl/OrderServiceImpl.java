package kg.shoro.crm.service.impl;

import jakarta.transaction.Transactional;
import kg.shoro.crm.exception.OrderNotFoundException;
import kg.shoro.crm.model.Customer;
import kg.shoro.crm.model.Order;
import kg.shoro.crm.model.Product;
import kg.shoro.crm.repository.OrderRepository;
import kg.shoro.crm.service.CustomerService;
import kg.shoro.crm.service.OrderService;
import kg.shoro.crm.service.ProductService;
import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.spring.shared.dto.response.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    private final AmqpTemplate amqpTemplate;
    @Value("${crm.exchange}")
    private String exchange;
    @Value("${crm.routing.order-created}")
    private String orderCreatedRoutingKey;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + id)
        );
    }

    public Order createOrder(CreateOrderRequest request) {
        Customer customer = customerService.getCustomerById(request.customerId());
        List<Product> productsByIds = productService.getAllProductsByIds(request.productIds());
        Order order = Order.builder()
                .customer(customer)
                .products(productsByIds)
                .build();
        Order savedOrder = orderRepository.save(order);
        Double total = 0.0;
        for (Product product : productsByIds) {
            total += product.getPrice();
        }
        amqpTemplate.convertAndSend(
                exchange,
                orderCreatedRoutingKey,
                new OrderCreatedEvent(savedOrder.getId(), customer.getId(), total));
        return savedOrder;
    }

    public Order updateOrder(Long id, UpdateOrderRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + id)
        );
        order.setCustomer(customerService.getCustomerById(request.customerId()));
        order.setProducts(productService.getAllProductsByIds(request.productIds()));
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + id)
        );
        orderRepository.delete(order);
    }

    @Transactional
    public void attachQrToOrder(Long orderId, String filePath) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        order.setQrPath(filePath);
        orderRepository.save(order);
    }

}
