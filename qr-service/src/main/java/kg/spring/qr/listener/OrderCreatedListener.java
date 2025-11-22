package kg.spring.qr.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.spring.qr.publisher.QrGeneratedPublisher;
import kg.spring.qr.service.QrGenerator;
import kg.spring.shared.dto.response.OrderCreatedEvent;
import kg.spring.shared.dto.response.QrGeneratedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private final QrGeneratedPublisher publisher;
    private final QrGenerator generator;
    private final ObjectMapper objectMapper;

    private final String orderCreatedQueue; // удалено использование SpEL

    public OrderCreatedListener(QrGeneratedPublisher publisher,
                                QrGenerator generator,
                                ObjectMapper objectMapper,
                                @Value("${queues.order-created}") String orderCreatedQueue) {
        this.publisher = publisher;
        this.generator = generator;
        this.objectMapper = objectMapper;
        this.orderCreatedQueue = orderCreatedQueue;
    }

    @RabbitListener(queues = "${queues.order-created}")
    public void onOrderCreated(OrderCreatedEvent event) throws Exception {
        String content = objectMapper.writeValueAsString(event);
        String filePath = generator.generateQrFile(event.getOrderId(), content);
        publisher.publish(new QrGeneratedEvent(event.getOrderId(), filePath));
    }
}
