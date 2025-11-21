package kg.shoro.crm.dto.request;

public record UpdateProductRequest(
        String name,
        String description,
        Double price,
        Integer quantity
) {
}
