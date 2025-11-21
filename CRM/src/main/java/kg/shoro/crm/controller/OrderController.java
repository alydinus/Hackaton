package kg.shoro.crm.controller;

import jakarta.validation.Valid;
import kg.shoro.crm.mapper.OrderMapper;
import kg.shoro.crm.service.OrderService;
import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.spring.shared.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return new ResponseEntity<>(
                orderMapper.toResponseList(orderService.getAllOrders()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return new ResponseEntity<>(
                orderMapper.toResponse(orderService.getOrderById(id)),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return new ResponseEntity<>(
                orderMapper.toResponse(orderService.createOrder(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @RequestBody @
            Valid UpdateOrderRequest request) {
        return new ResponseEntity<>(
                orderMapper.toResponse(orderService.updateOrder(id, request)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
