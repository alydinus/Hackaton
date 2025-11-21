package kg.shoro.crm.dto.request;

public record CreateCustomerRequest(
        String name,
        Double debt
) {
}
