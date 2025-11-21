package kg.aiu.telegram_sevrice.service.serviceImpl;

import kg.aiu.telegram_sevrice.service.TelServiceClient;
import org.springframework.stereotype.Service;

@Service
public class TelServiceClientImpl implements TelServiceClient {
//    public List<Product> getAllProducts() {
//        return webClient.get()
//                .uri("/api/products")
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
//                .block();
//    }
//
//    public Product getProductById(Long id) {
//        return webClient.get()
//                .uri("/api/products/{id}", id)
//                .retrieve()
//                .bodyToMono(Product.class)
//                .block();
//    }
//
//    public Product createProduct(Product product) {
//        return webClient.post()
//                .uri("/api/products")
//                .bodyValue(product)
//                .retrieve()
//                .bodyToMono(Product.class)
//                .block();
//    }
//
//    public Product updateProduct(Long id, Product product) {
//        return webClient.put()
//                .uri("/api/products/{id}", id)
//                .bodyValue(product)
//                .retrieve()
//                .bodyToMono(Product.class)
//                .block();
//    }
//
//    public void deleteProduct(Long id) {
//        webClient.delete()
//                .uri("/api/products/{id}", id)
//                .retrieve()
//                .toBodilessEntity()
//                .block();
//    }
//
//    public List<Product> searchProducts(String query) {
//        return webClient.get()
//                .uri("/api/products/search?query={query}", query)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
//                .block();
//    }
}
