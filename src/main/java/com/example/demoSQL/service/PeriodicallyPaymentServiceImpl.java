package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.PeriodicallyPayment;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.enums.SubscriptionStatus;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.PeriodicallyPaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.annotations.DialectOverride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demoSQL.enums.Period.*;

@Service
@Transactional
public class PeriodicallyPaymentServiceImpl implements PeriodicallyPaymentService {

    @Autowired
    private PeriodicallyPaymentRepository periodicallyPaymentRepository;

    @Autowired
    private AccountRepository accountRepository;



    @Override
    public ApiResponse<Object> createPeriodicallyPayment(PeriodicallyPaymentDTO periodicallyPaymentDTO) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(periodicallyPaymentDTO.getAccountId());
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Account account = optionalAccount.get();
            PeriodicallyPayment periodicallyPayment = new PeriodicallyPayment();
            periodicallyPayment.setAccount(account);
            periodicallyPayment.setDescription(periodicallyPaymentDTO.getDescription());
            periodicallyPayment.setAmount(periodicallyPaymentDTO.getAmount());
            periodicallyPayment.setPeriod(periodicallyPaymentDTO.getPeriod());
            periodicallyPaymentRepository.save(periodicallyPayment);

            return new ApiResponse<>(toPeriodicallyPaymentDTO(periodicallyPayment), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    public ApiResponse<Object> updatePeriodicallyPayment(Long id, PeriodicallyPaymentUpdateDTO periodicallyPaymentUpdateDTO){
        try{
            Optional<PeriodicallyPayment> optionalPeriodicallyPayment = periodicallyPaymentRepository.findById(id);
            if(optionalPeriodicallyPayment.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            PeriodicallyPayment periodicallyPayment = optionalPeriodicallyPayment.get();

            periodicallyPayment.setAmount(periodicallyPaymentUpdateDTO.getAmount());
            periodicallyPayment.setStatus(periodicallyPaymentUpdateDTO.getStatus());
            periodicallyPaymentRepository.save(periodicallyPayment);
            return new ApiResponse<>(toPeriodicallyPaymentDTO(periodicallyPayment), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }



    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getPeriodicallyPaymentById(Long id) {
        try {
            Optional<PeriodicallyPayment> optionalPeriodicallyPayment = periodicallyPaymentRepository.findById(id);
            if(optionalPeriodicallyPayment.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            PeriodicallyPayment periodicallyPayment = optionalPeriodicallyPayment.get();
            return new ApiResponse<>(toPeriodicallyPaymentDTO(periodicallyPayment), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getPeriodicallyPaymentByAccountId(Long id, Pageable pageable){
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<PeriodicallyPayment> periodicallyPayment = periodicallyPaymentRepository.findByAccountId(id, pageable);
            return new ApiResponse<>(periodicallyPayment.map(this::toPeriodicallyPaymentDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable) {
        try {
            Page<PeriodicallyPayment> periodicallyPayment = periodicallyPaymentRepository.findByAccountIdAndStatus(accountId, status, pageable);
            return new ApiResponse<>(periodicallyPayment.map(this::toPeriodicallyPaymentDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight
    public void processingPayment() {
        List<PeriodicallyPayment> payments = periodicallyPaymentRepository.findAll();

        for(PeriodicallyPayment payment : payments){
            if(payment.getEndedAt() == null && payment.getEndedAt().isBefore(LocalDateTime.now())){

                updateEndedAt(payment);
                Account account = payment.getAccount();
                account.setBalance(account.getBalance().subtract(payment.getAmount()));
                accountRepository.save(account);
                periodicallyPaymentRepository.save(payment);
            }
        }
    }
    //helper

    private PeriodicallyPaymentDTO toPeriodicallyPaymentDTO(PeriodicallyPayment periodicallyPayment) {
        return PeriodicallyPaymentDTO.builder()
                .accountId(periodicallyPayment.getAccount().getId())
                .description(periodicallyPayment.getDescription())
                .amount(periodicallyPayment.getAmount())
                .period(periodicallyPayment.getPeriod())
                .startedAt(periodicallyPayment.getStartedAt())
                .build();
    }
    private void updateEndedAt(PeriodicallyPayment periodicallyPayment) {
        switch(periodicallyPayment.getPeriod()) {
            case WEEKLY -> periodicallyPayment.setEndedAt(LocalDateTime.now().plusWeeks(1));
            case MONTHLY -> periodicallyPayment.setEndedAt(LocalDateTime.now().plusMonths(1));
            case YEARLY -> periodicallyPayment.setEndedAt(LocalDateTime.now().plusYears(1));
            default -> throw new RuntimeException("Unexpected value: " + periodicallyPayment.getPeriod());
        }
    }
}
