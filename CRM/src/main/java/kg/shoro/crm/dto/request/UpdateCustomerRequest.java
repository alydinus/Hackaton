package kg.shoro.crm.dto.request;

public record UpdateCustomerRequest(
        String name,
        Double debt
) {
}
