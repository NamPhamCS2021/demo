package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;

import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.enums.SubscriptionStatus;
import com.example.demoSQL.service.PeriodicallyPaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@Repository
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PeriodicallyPaymentController {

    private final PeriodicallyPaymentService periodicallyPaymentService;

    @PreAuthorize("@authSecurity.isOwnerOfPayment(#id)")
    @GetMapping("/{id}")
    public ApiResponse<Object> findById(@PathVariable Long id){
        return periodicallyPaymentService.getPeriodicallyPaymentById(id);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/{id}")
    public ApiResponse<Object> findByAccountId(@PathVariable Long id,
                                                                        @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return periodicallyPaymentService.getPeriodicallyPaymentByAccountId(id, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/status")
    public ApiResponse<Object> findByAccountIdAndStatus(@RequestParam Long id,
                                                        @RequestParam SubscriptionStatus status,
                                                        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return periodicallyPaymentService.getByAccountIdAndStatus(id, status, pageable);

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<Object> update(@PathVariable Long id, @RequestBody PeriodicallyPaymentUpdateDTO payment){
        return periodicallyPaymentService.updatePeriodicallyPayment(id, payment);
    }
}
