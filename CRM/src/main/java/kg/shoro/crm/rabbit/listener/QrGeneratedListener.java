package kg.shoro.crm.rabbit.listener;

import kg.shoro.crm.service.OrderService;
import kg.spring.shared.dto.response.QrGeneratedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QrGeneratedListener {

    private final OrderService orderService;

    @RabbitListener(queues = "qr.generated")
    public void onQrGenerated(QrGeneratedEvent event) {
        orderService.attachQrToOrder(event.orderId(), event.filePath());
        System.out.println("QR saved for order " + event.orderId());
    }
}

