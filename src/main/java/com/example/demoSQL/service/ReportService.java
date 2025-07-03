package com.example.demoSQL.service;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    Map<String, Object> generateReport(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> generateReportByAccount(Long accountId, LocalDateTime start, LocalDateTime end);

    Map<String, Object> generateAccountReport(LocalDateTime start, LocalDateTime end);
}
