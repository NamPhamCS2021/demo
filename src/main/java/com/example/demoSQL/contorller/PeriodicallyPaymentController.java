package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.enums.SubscriptionStatus;
import com.example.demoSQL.service.PeriodicallyPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PeriodicallyPaymentController {

    private final PeriodicallyPaymentService periodicallyPaymentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> findById(@PathVariable Long id){
        ApiResponse<Object> payment = periodicallyPaymentService.getPeriodicallyPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<ApiResponse<Object>> findByAccountId(@PathVariable Long id,
                                                                        @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        ApiResponse<Object> payment = periodicallyPaymentService.getPeriodicallyPaymentByAccountId(id, pageable);
        return ResponseEntity.ok(payment);
    }
    @GetMapping("/account/status")
    public ResponseEntity<ApiResponse<Object>> findByAccountIdAndStatus(@RequestParam Long id,
                                                                                 @RequestParam SubscriptionStatus status,
                                                                                 @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        ApiResponse<Object> payment = periodicallyPaymentService.getByAccountIdAndStatus(id, status, pageable);
        return ResponseEntity.ok(payment);
    }
}
