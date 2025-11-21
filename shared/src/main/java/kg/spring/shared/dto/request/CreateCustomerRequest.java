package kg.spring.shared.dto.request;

public record CreateCustomerRequest(
        String name,
        Double debt
) {
}
