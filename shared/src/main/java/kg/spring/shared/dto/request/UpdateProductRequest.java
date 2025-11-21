package kg.spring.shared.dto.request;

public record UpdateProductRequest(
        String name,
        String description,
        Double price,
        Integer quantity
) {
}
