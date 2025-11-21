package kg.shoro.crm.service;

import kg.shoro.crm.model.Product;
import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);

    List<Product> getAllProductsByIds(List<Long> longs);
}
