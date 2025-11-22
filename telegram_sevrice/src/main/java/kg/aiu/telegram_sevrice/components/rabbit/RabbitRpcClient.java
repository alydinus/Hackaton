package kg.aiu.telegram_sevrice.components.rabbit;

import kg.aiu.telegram_sevrice.configuration.RabbitConfig;
import kg.spring.shared.dto.response.OrderResponse;
import kg.spring.shared.dto.response.ProductResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@Component
public class RabbitRpcClient {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitRpcClient(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.rabbitTemplate.setReplyTimeout(30000);
    }

    public OrderResponse getOrderById(Long orderId) {
        try {
            byte[] response = (byte[]) rabbitTemplate.convertSendAndReceive(
                    RabbitConfig.RPC_EXCHANGE,
                    RabbitConfig.RPC_ORDER_QUEUE,
                    "getById:" + orderId
            );

            if (response != null && response.length > 0) {
                return objectMapper.readValue(response, OrderResponse.class);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting order by ID: " + orderId, e);
        }
    }

    public List<OrderResponse> getAllOrders() {
        try {
            byte[] response = (byte[]) rabbitTemplate.convertSendAndReceive(
                    RabbitConfig.RPC_EXCHANGE,
                    RabbitConfig.RPC_ORDERS_LIST_QUEUE,
                    "getAll"
            );

            if (response != null && response.length > 0) {
                return objectMapper.readValue(response,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, OrderResponse.class));
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all orders", e);
        }
    }

    public ProductResponse getProductById(Long productId) {
        try {
            byte[] response = (byte[]) rabbitTemplate.convertSendAndReceive(
                    RabbitConfig.RPC_EXCHANGE,
                    RabbitConfig.RPC_PRODUCT_QUEUE,
                    "getById:" + productId
            );

            if (response != null && response.length > 0) {
                return objectMapper.readValue(response, ProductResponse.class);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting product by ID: " + productId, e);
        }
    }

    public List<ProductResponse> getAllProducts() {
        try {
            byte[] response = (byte[]) rabbitTemplate.convertSendAndReceive(
                    RabbitConfig.RPC_EXCHANGE,
                    RabbitConfig.RPC_PRODUCT_QUEUE,
                    "getAll"
            );

            if (response != null && response.length > 0) {
                return objectMapper.readValue(response,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ProductResponse.class));
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all products", e);
        }
    }
}