package kg.shoro.crm.service.impl;

import kg.shoro.crm.service.OrderService;
import kg.shoro.crm.service.ReportService;
import kg.spring.shared.dto.response.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderService orderService;

    public ReportResponse generateReport(Long days) {
        Long totalOrders = orderService.countOrdersInLastDays(days);
        Double totalRevenue = orderService.calculateTotalRevenueInLastDays(days);
        Double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        return new ReportResponse(
                days,
                totalOrders,
                totalRevenue,
                averageOrderValue
        );
    }
}
