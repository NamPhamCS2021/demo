package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.periodicallypayment.PeriodicalPaymentSearchDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicalPaymentUserSearchDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PeriodicalPaymentService {

    ApiResponse<Object> createPeriodicalPayment(PeriodicallyPaymentDTO periodicallyPaymentDTO);
    ApiResponse<Object> updatePeriodicalPayment(UUID publicId, PeriodicallyPaymentUpdateDTO periodicallyPaymentUpdateDTO);
    ApiResponse<Object> getPeriodicalPaymentById(UUID publicId);;
    ApiResponse<Object> getPeriodicalPaymentByAccountNumber(String accountNumber, Pageable pageable);
    ApiResponse<Object> search(PeriodicalPaymentSearchDTO dto, Pageable pageable);
    ApiResponse<Object> selfSearch(String accountNumber, PeriodicalPaymentUserSearchDTO dto, Pageable pageable);
    void processingPayment();
}
