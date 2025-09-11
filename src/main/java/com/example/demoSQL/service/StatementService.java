package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import org.springframework.data.domain.Pageable;

public interface StatementService {
    public ApiResponse<Object> getMonthlyStatement(Long customerId, int year, int month, Pageable pageable);
}
