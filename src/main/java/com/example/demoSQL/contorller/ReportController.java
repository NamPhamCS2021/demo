package com.example.demoSQL.contorller;

import com.example.demoSQL.service.ReportService;
import com.example.demoSQL.util.PDFExportUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    @GetMapping("/transaction-report")
    public Map<String, Object> getTransactionReport(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                    @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end){
        return reportService.generateReport(start, end);
    }

    @GetMapping("/report-for-account/{id}")
    public Map<String, Object> generateReportByAccount(@PathVariable Long id, @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                       @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end) {
        return reportService.generateReportByAccount(id, start, end);
    }

    @GetMapping("/account-report")
    public Map<String, Object> generateAccountReport(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                     @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end) {
        return reportService.generateAccountReport(start, end);
    }

    @GetMapping("/transaction-report-pdf")
    public void exportTransactionReportToPdf(HttpServletResponse response, @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                             @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end) {
        try{
            Map<String, Object> reportMap = reportService.generateReport(start, end);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=transaction-report.pdf");

            PDFExportUtil.exportPDF(response.getOutputStream(), reportMap);
        } catch (Exception e) {
            throw new RuntimeException("failed to export transaction report", e);
        }
    }

    @GetMapping("/transaction-report-by-accoount-pdf")
    public void exportTransactionReportByAccountToPdf(HttpServletResponse response, @RequestParam Long id, @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                             @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end) {
        try{
            Map<String, Object> reportMap = reportService.generateReportByAccount(id, start, end);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=transaction-report.pdf");

            PDFExportUtil.exportPDF(response.getOutputStream(), reportMap);
        } catch (Exception e) {
            throw new RuntimeException("failed to export transaction report", e);
        }
    }

    @GetMapping("/account-report-pdf")
    public void exportAccountReportToPdf(HttpServletResponse response, @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                             @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end) {
        try{
            Map<String, Object> reportMap = reportService.generateAccountReport(start, end);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=transaction-report.pdf");

            PDFExportUtil.exportPDF(response.getOutputStream(), reportMap);
        } catch (Exception e) {
            throw new RuntimeException("failed to export transaction report", e);
        }
    }

}
