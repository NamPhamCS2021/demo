package com.example.demoSQL.controller;

import com.example.demoSQL.dto.ApiResponse;

import com.example.demoSQL.dto.periodicallypayment.PeriodicalPaymentSearchDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicalPaymentUserSearchDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.enums.SubscriptionStatus;
import com.example.demoSQL.service.PeriodicalPaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
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
public class PeriodicalPaymentController {

    private final PeriodicalPaymentService periodicalPaymentService;

    @PreAuthorize("@authSecurity.isOwnerOfPayment(#id)")
    @GetMapping("/{id}")
    public ApiResponse<Object> findById(@PathVariable Long id){
        return periodicalPaymentService.getPeriodicalPaymentById(id);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/{id}")
    public ApiResponse<Object> findByAccountId(@PathVariable Long id,
                                               @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return periodicalPaymentService.getPeriodicalPaymentByAccountId(id, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/status")
    public ApiResponse<Object> findByAccountIdAndStatus(@RequestParam Long id,
                                                        @RequestParam SubscriptionStatus status,
                                                        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return periodicalPaymentService.getByAccountIdAndStatus(id, status, pageable);

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/search")
    public ApiResponse<Object> search(@Valid @RequestBody PeriodicalPaymentSearchDTO periodicalPaymentSearchDTO,
                                      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return periodicalPaymentService.searchPeriodicalPayment(periodicalPaymentSearchDTO, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @PostMapping("/search/{id}")
    public ApiResponse<Object> searchSelfPeriodicalPayment(@PathVariable Long id, @Valid @RequestBody PeriodicalPaymentUserSearchDTO dto,
                                                           @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return periodicalPaymentService.selfSearchPeriodicalPayment(id, dto, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<Object> update(@PathVariable Long id, @Valid @RequestBody PeriodicallyPaymentUpdateDTO payment){
        return periodicalPaymentService.updatePeriodicalPayment(id, payment);
    }
}
