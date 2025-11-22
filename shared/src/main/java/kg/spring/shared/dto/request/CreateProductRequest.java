package kg.spring.shared.dto.request;

public record CreateProductRequest(
        Long tempId,
        String name,
        String description,
        Double price,
        Integer quantity
) {
}
