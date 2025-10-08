package com.example.demoSQL.controller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.service.StatementService;
import com.example.demoSQL.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;
    private final TransactionService transactionService;

    @GetMapping("/{customerId}")
    @PreAuthorize("@authSecurity.isSelfCustomer(#customerId)")
    public ApiResponse<Object> getMonthlyStatements(@PathVariable("customerId") UUID customerId, @RequestParam int year, @RequestParam int month,
                                                    @PageableDefault(size = 20) Pageable pageable) {
        return statementService.getMonthlyStatement(customerId, year, month, pageable);
    }

}
