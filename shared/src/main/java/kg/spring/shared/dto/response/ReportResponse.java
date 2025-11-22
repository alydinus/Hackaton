package kg.spring.shared.dto.response;

public record ReportResponse(
    Long days,
    Long totalOrders,
    Double totalRevenue,
    Double averageOrderValue
) {
}
