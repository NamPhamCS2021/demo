package com.example.demoSQL.service;

import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentDTO;
import com.example.demoSQL.dto.periodicallypayment.PeriodicallyPaymentUpdateDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.PeriodicallyPayment;
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

import static com.example.demoSQL.enums.Period.*;

@Service
@Transactional
public class PeriodicallyPaymentServiceImpl implements PeriodicallyPaymentService {

    @Autowired
    private PeriodicallyPaymentRepository periodicallyPaymentRepository;

    @Autowired
    private AccountRepository accountRepository;

    public PeriodicallyPaymentServiceImpl(PeriodicallyPaymentRepository periodicallyPaymentRepository, AccountRepository accountRepository) {
        this.periodicallyPaymentRepository = periodicallyPaymentRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public PeriodicallyPaymentDTO createPeriodicallyPayment(PeriodicallyPaymentDTO periodicallyPaymentDTO) {
        Account account = accountRepository.findById(periodicallyPaymentDTO.getAccountId()).orElseThrow(() ->  new EntityNotFoundException("Account with id "+periodicallyPaymentDTO.getAccountId()+" not found"));
        PeriodicallyPayment periodicallyPayment = new PeriodicallyPayment();
        periodicallyPayment.setAccount(account);
        periodicallyPayment.setDescription(periodicallyPaymentDTO.getDescription());
        periodicallyPayment.setAmount(periodicallyPaymentDTO.getAmount());
        periodicallyPayment.setPeriod(periodicallyPaymentDTO.getPeriod());
        periodicallyPaymentRepository.save(periodicallyPayment);

        return toPeriodicallyPaymentDTO(periodicallyPayment);
    }

    @Override
    public PeriodicallyPaymentDTO updatePeriodicallyPayment(Long id, PeriodicallyPaymentUpdateDTO periodicallyPaymentUpdateDTO){
        PeriodicallyPayment periodicallyPayment = periodicallyPaymentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment with id:" + id + "not found"));

        periodicallyPayment.setAmount(periodicallyPaymentUpdateDTO.getAmount());
        periodicallyPayment.setStatus(periodicallyPaymentUpdateDTO.getStatus());
        periodicallyPaymentRepository.save(periodicallyPayment);

        return toPeriodicallyPaymentDTO(periodicallyPayment) ;
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodicallyPaymentDTO getPeriodicallyPaymentById(Long id) {
        PeriodicallyPayment periodicallyPayment = periodicallyPaymentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment with id:" + id + "not found"));
        return toPeriodicallyPaymentDTO(periodicallyPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PeriodicallyPaymentDTO> getPeriodicallyPaymentByAccountId(Long id, Pageable pageable){
        Page<PeriodicallyPayment> periodicallyPayment = periodicallyPaymentRepository.findByAccountId(id, pageable);
        return periodicallyPayment.map(this::toPeriodicallyPaymentDTO) ;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PeriodicallyPaymentDTO> getByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable) {
        Page<PeriodicallyPayment> periodicallyPayment = periodicallyPaymentRepository.findByAccountIdAndStatus(accountId, status, pageable);
        return periodicallyPayment.map(this::toPeriodicallyPaymentDTO) ;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight
    public void processingPayment() {
        List<PeriodicallyPayment> payments = periodicallyPaymentRepository.findAll();

        for(PeriodicallyPayment payment : payments){
            if(payment.getEndedAt() == null && payment.getEndedAt().isBefore(LocalDateTime.now())){

                updateEndedAt(payment);
                Account account = payment.getAccount();
                account.getBalance().subtract(payment.getAmount());
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
    private PeriodicallyPayment updateEndedAt(PeriodicallyPayment periodicallyPayment) {
        switch(periodicallyPayment.getPeriod()) {
            case WEEKLY -> periodicallyPayment.setEndedAt(LocalDateTime.now().plusWeeks(1));
            case MONTHLY -> periodicallyPayment.setEndedAt(LocalDateTime.now().plusMonths(1));
            case YEARLY -> periodicallyPayment.setEndedAt(LocalDateTime.now().plusYears(1));
            default -> throw new RuntimeException("Unexpected value: " + periodicallyPayment.getPeriod());
        }
        return periodicallyPayment;
    }
}
