package kg.shoro.crm.service;

import kg.spring.shared.dto.response.ReportResponse;

public interface ReportService {
    ReportResponse generateReport(Long days);
}
