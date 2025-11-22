package kg.shoro.crm.controller;

import kg.shoro.crm.service.ReportService;
import kg.spring.shared.dto.response.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{days}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Long days) {
        return ResponseEntity.ok(
                reportService.generateReport(days)
        );
    }
}
