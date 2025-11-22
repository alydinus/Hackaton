package kg.aiu.telegram_sevrice.components.rabbit;

import kg.aiu.telegram_sevrice.configuration.RabbitConfig;
import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.DeleteOrderRequest;
import kg.spring.shared.dto.request.DeleteProductRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.spring.shared.dto.request.UpdateProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitSender {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final RabbitTemplate rabbitTemplate;

    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrder(CreateOrderRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CRM_EXCHANGE,
                RabbitConfig.ORDER_ROUTING_KEY,
                request
        );
    }
    public void updateOrder(UpdateOrderRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CRM_EXCHANGE,
                RabbitConfig.ORDER_ROUTING_KEY,
                request
        );
    }

    public void sendProduct(CreateProductRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CRM_EXCHANGE,
                RabbitConfig.PRODUCT_ROUTING_KEY,
                request
        );
    }

    public void updateProduct(UpdateProductRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CRM_EXCHANGE,
                RabbitConfig.PRODUCT_ROUTING_KEY,
                request
        );
    }

    public void deleteProduct(DeleteProductRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CRM_EXCHANGE,
                RabbitConfig.PRODUCT_ROUTING_KEY,
                request
        );
    }

    public void deleteOrder(DeleteOrderRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CRM_EXCHANGE,
                RabbitConfig.PRODUCT_ROUTING_KEY,
                request
        );
    }


}
