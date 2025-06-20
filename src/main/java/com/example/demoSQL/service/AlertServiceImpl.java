package com.example.demoSQL.service;


import com.example.demoSQL.dto.alert.AlertDTO;
import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AlertRepository;
import com.example.demoSQL.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private Executor virtualExecutor;

    public AlertServiceImpl(AlertRepository alertRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.alertRepository = alertRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void detectAbnormalTransactions() {

        List<Transaction> uncheckedTransactions = transactionRepository.findByCheckedFalse();

        List<CompletableFuture<Boolean>> futures = uncheckedTransactions.stream()
                .map(transaction -> CompletableFuture.supplyAsync(() -> {
                    try {
                        boolean result = processTransaction(transaction);
                        transaction.setChecked(true);
                        return result;
                    } catch (Exception e) {
                        System.out.println("Error processing transaction with id " + transaction.getId());
                        return false;
                    } finally {
                        transactionRepository.save(transaction);
                        System.out.println("Transaction with id " + transaction.getId() + " processed");
                        System.out.println("---------------------------------------");
                    }
                }, virtualExecutor)).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    //helper to process transactions
    private boolean processTransaction(Transaction transaction) {
        boolean notNormal = false;

        if(transaction.getAmount().compareTo(transaction.getAccount().getAccountLimit()) > 0){
            createAlert(transaction, "Transaction limit exceeded!",AlertType.LARGE_AMOUNT);
            notNormal = true;
        }

        LocalDateTime transactionTime = transaction.getTimestamp();

        LocalDateTime startTime = transaction.getTimestamp().minusSeconds(30);

        LocalDateTime endTime = transaction.getTimestamp().plusSeconds(30);

        List<Transaction> transactionsOfAccountBetweenTime = transactionRepository.findBetweenTimeByAccount(transaction.getAccount().getId(), startTime, endTime);
        if(transactionsOfAccountBetweenTime.size() > 3){
            for(Transaction t : transactionsOfAccountBetweenTime){
                createAlert(t, "Too many transactions in a short period!",AlertType.TOO_MANY_TRANSACTIONS);
            }

            notNormal = true;
        }
        return notNormal;
    }

    @Override
    public Page<AlertDTO> getAll(Pageable pageable) {
        Page<Alert> alerts = alertRepository.findAll(pageable);
        return alerts.map(this::toAlertDTO);
    }

    private Alert createAlert(Transaction transaction, String description, AlertType type) {
        Alert alert = new Alert();
        alert.setTransaction(transaction);
        alert.setDescription(description);
        alert.setType(type);

        return alert;
    }
    @Override
    public Page<AlertDTO> getByTransactionId(Long transactionId, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByTransactionId(transactionId, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByTransactionIdAndType(Long transactionId, AlertType type, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByTransactionIdAndType(transactionId, type, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByTransactionIdAndStatus(Long transactionId, AlertStatus status, Pageable pageable){

        Page<Alert> alerts = alertRepository.findByTransactionIdAndStatus(transactionId, status, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByTransactionIdAndTypeAndStatus(Long transactionId, AlertType type, AlertStatus status, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByTransactionIdAndTypeAndStatus(transactionId, type, status, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByAccountId(Long accountId, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByAccountId(accountId, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByAccountIdAndType(Long accountId, AlertType type, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByAccountIdAndType(accountId, type, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByAccountIdAndStatus(Long accountId, AlertStatus status, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByAccountIdAndStatus(accountId, status, pageable);
        return alerts.map(this::toAlertDTO);
    }

    @Override
    public Page<AlertDTO> getByAccountIdAndTypeAndStatus(Long accountId, AlertType type, AlertStatus status, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findByAccountIdAndTypeAndStatus(accountId, type, status, pageable);
        return alerts.map(this::toAlertDTO);
    }

    private AlertDTO toAlertDTO(Alert alert) {
        return AlertDTO.builder()
                .accountId(alert.getTransaction().getAccount().getId())
                .transactionId(alert.getTransaction().getId())
                .description(alert.getDescription())
                .type(alert.getType())
                .status(alert.getStatus())
                .timestamp(alert.getTimestamp()).build();
    }
}
