package kg.shoro.crm.rabbit.publisher;

import kg.spring.shared.dto.response.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String orderCreatedRoutingKey;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${crm.exchange}") String exchangeName,
                               @Value("${crm.routing.order-created}") String orderCreatedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.orderCreatedRoutingKey = orderCreatedRoutingKey;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, orderCreatedRoutingKey, event);
    }
}