package kg.shoro.crm.dto.request;

public record CreateProductRequest(
        String name,
        String description,
        Double price,
        Integer quantity
) {
}
