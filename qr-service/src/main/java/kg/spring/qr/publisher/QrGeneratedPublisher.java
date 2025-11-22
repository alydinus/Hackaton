package kg.spring.qr.publisher;

import kg.spring.shared.dto.response.QrGeneratedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QrGeneratedPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String qrGeneratedRoutingKey;

    public QrGeneratedPublisher(RabbitTemplate rabbitTemplate,
                                @Value("${exchange}") String exchangeName,
                                @Value("${queues.qr-generated}") String qrGeneratedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.qrGeneratedRoutingKey = qrGeneratedRoutingKey;
    }

    public void publish(QrGeneratedEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, qrGeneratedRoutingKey, event);
    }
}
