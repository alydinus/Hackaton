package kg.aiu.telegram_sevrice.service.serviceImpl;

import kg.aiu.telegram_sevrice.service.OrderServiceClient;
import kg.spring.shared.dto.request.CreateOrderRequest;
import kg.spring.shared.dto.request.UpdateOrderRequest;
import kg.spring.shared.dto.response.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OrderServiceClientImpl implements OrderServiceClient {

    private final WebClient webClient;

    public OrderServiceClientImpl(@Value("${crm.service.url}") String crmServiceUrl,
                                  @Value("${crm.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(crmServiceUrl)
                .defaultHeader("X-API-KEY", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<OrderResponse> getAllOrderResponses() {
        return webClient.get()
                .uri("/api/orders")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<OrderResponse>>() {})
                .block();
    }

    public OrderResponse getOrderResponseById(Long id) {
        return webClient.get()
                .uri("/api/orders/{id}", id)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        return webClient.post()
                .uri("/api/orders")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();
    }

    public OrderResponse updateOrder(Long id, UpdateOrderRequest request) {
        return webClient.put()
                .uri("/api/orders/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();
    }

    public void deleteOrder(Long id) {
        webClient.delete()
                .uri("/api/orders/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
