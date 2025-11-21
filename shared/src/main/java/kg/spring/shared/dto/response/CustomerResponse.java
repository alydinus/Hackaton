package kg.spring.shared.dto.response;

public record CustomerResponse(
        Long id,
        String name,
        Double debt
) {
}
