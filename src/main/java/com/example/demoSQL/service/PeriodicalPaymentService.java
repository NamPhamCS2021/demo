package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.periodicallypayment.PeriodicalPaymentSearchDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicalPaymentUserSearchDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.enums.SubscriptionStatus;
import org.springframework.data.domain.Pageable;

public interface PeriodicalPaymentService {

    ApiResponse<Object> createPeriodicalPayment(PeriodicallyPaymentDTO periodicallyPaymentDTO);
    ApiResponse<Object> updatePeriodicalPayment(Long id, PeriodicallyPaymentUpdateDTO periodicallyPaymentUpdateDTO);
    ApiResponse<Object> getPeriodicalPaymentById(Long id);;
    ApiResponse<Object> getPeriodicalPaymentByAccountId(Long id, Pageable pageable);
    ApiResponse<Object> getByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable);
    ApiResponse<Object> searchPeriodicalPayment(PeriodicalPaymentSearchDTO dto, Pageable pageable);
    ApiResponse<Object> selfSearchPeriodicalPayment(Long id, PeriodicalPaymentUserSearchDTO dto, Pageable pageable);
    void processingPayment();
}
