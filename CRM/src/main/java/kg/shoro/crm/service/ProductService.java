package kg.shoro.crm.service;

import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.UpdateProductRequest;
import kg.shoro.crm.model.OrderProduct;
import kg.shoro.crm.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);

    List<OrderProduct> getProductsByIds(List<Long> longs);
}
