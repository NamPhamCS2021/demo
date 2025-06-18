package com.example.demoSQL.service;

import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.entity.PeriodicallyPayment;
import com.example.demoSQL.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PeriodicallyPaymentService {

    PeriodicallyPaymentDTO createPeriodicallyPayment(PeriodicallyPaymentDTO periodicallyPaymentDTO);
    PeriodicallyPaymentDTO updatePeriodicallyPayment(Long id, PeriodicallyPaymentUpdateDTO periodicallyPaymentUpdateDTO);
    PeriodicallyPaymentDTO getPeriodicallyPaymentById(Long id);;
    Page<PeriodicallyPaymentDTO> getPeriodicallyPaymentByAccountId(Long id, Pageable pageable);
    Page<PeriodicallyPaymentDTO> getByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable);
    void processingPayment();
}
