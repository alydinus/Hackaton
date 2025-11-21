package kg.shoro.crm.service;

import kg.shoro.crm.dto.request.CreateProductRequest;
import kg.shoro.crm.dto.request.UpdateProductRequest;
import kg.shoro.crm.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
}
