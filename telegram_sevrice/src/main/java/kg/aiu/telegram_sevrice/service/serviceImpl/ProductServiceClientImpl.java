package kg.aiu.telegram_sevrice.service.serviceImpl;

import kg.aiu.telegram_sevrice.service.ProductServiceClient;
import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.UpdateProductRequest;
import kg.spring.shared.dto.response.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProductServiceClientImpl implements ProductServiceClient {

    private final WebClient webClient;

    public ProductServiceClientImpl(@Value("${crm.service.url}") String crmServiceUrl,
                                  @Value("${crm.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(crmServiceUrl)
                .defaultHeader("X-API-KEY", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<ProductResponse> getAllProductResponses() {
        return webClient.get()
                .uri("/api/products")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductResponse>>() {})
                .block();
    }

    public ProductResponse getProductResponseById(Long id) {
        return webClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        return webClient.post()
                .uri("/api/products")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }

    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        return webClient.put()
                .uri("/api/products/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }

    public void deleteProduct(Long id) {
        webClient.delete()
                .uri("/api/products/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
