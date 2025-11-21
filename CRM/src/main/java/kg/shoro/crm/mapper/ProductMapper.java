package kg.shoro.crm.mapper;

import kg.shoro.crm.model.Product;
import kg.spring.shared.dto.response.ProductResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);
    List<ProductResponse> toResponseList(List<Product> products);
}
