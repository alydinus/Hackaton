package kg.spring.shared.dto.response;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Integer quantity,
        Double sum
) {
}
