package kg.spring.shared.dto.request;

public record UpdateCustomerRequest(
        String name,
        Double debt
) {
}
