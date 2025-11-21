package kg.spring.shared.dto.request;

public record CreateProductRequest(
        String name,
        String description,
        Double price,
        Integer quantity
) {
}
