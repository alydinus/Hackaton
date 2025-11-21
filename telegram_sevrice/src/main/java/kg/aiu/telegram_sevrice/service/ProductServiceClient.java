package kg.aiu.telegram_sevrice.service;

import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.UpdateProductRequest;
import kg.spring.shared.dto.response.ProductResponse;

import java.util.List;

public interface ProductServiceClient {
    List<ProductResponse> getAllProductResponses();
    ProductResponse getProductResponseById(Long id);
    ProductResponse updateProduct(Long id, UpdateProductRequest request);
    ProductResponse createProduct(CreateProductRequest request);
    void deleteProduct(Long id);


}
