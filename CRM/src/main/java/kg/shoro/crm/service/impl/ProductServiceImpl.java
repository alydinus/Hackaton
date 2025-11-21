package kg.shoro.crm.service.impl;

import kg.spring.shared.dto.request.CreateProductRequest;
import kg.spring.shared.dto.request.UpdateProductRequest;
import kg.shoro.crm.exception.ProductNotFoundException;
import kg.shoro.crm.model.OrderProduct;
import kg.shoro.crm.model.Product;
import kg.shoro.crm.repository.ProductRepository;
import kg.shoro.crm.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id " + id)
        );
    }

    public Product createProduct(CreateProductRequest request) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .quantity(request.quantity())
                .build();
    }

    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id " + id)
        );
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id " + id)
        );
        productRepository.delete(product);
    }

    public List<OrderProduct> getProductsByIds(List<Long> longs) {
        return productRepository.findAllByIdIn(longs);
    }
}
