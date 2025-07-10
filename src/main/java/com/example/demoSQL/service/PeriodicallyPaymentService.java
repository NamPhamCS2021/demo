package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.entity.PeriodicallyPayment;
import com.example.demoSQL.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PeriodicallyPaymentService {

    ApiResponse<Object> createPeriodicallyPayment(PeriodicallyPaymentDTO periodicallyPaymentDTO);
    ApiResponse<Object> updatePeriodicallyPayment(Long id, PeriodicallyPaymentUpdateDTO periodicallyPaymentUpdateDTO);
    ApiResponse<Object> getPeriodicallyPaymentById(Long id);;
    ApiResponse<Object> getPeriodicallyPaymentByAccountId(Long id, Pageable pageable);
    ApiResponse<Object> getByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable);
    void processingPayment();
}
