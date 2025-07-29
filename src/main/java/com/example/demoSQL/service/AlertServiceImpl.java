package com.example.demoSQL.service;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.alert.AlertDTO;
import com.example.demoSQL.dto.alert.AlertSearchDTO;
import com.example.demoSQL.dto.alert.AlertUserSearchDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AlertRepository;
import com.example.demoSQL.repository.TransactionRepository;
import com.example.demoSQL.specification.AlertSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Transactional(readOnly = true)
    public ApiResponse<Object> getAll(Pageable pageable) {
        try{
            Page<Alert> alerts = alertRepository.findAll(pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    private Alert createAlert(Transaction transaction, String description, AlertType type) {
        Alert alert = new Alert();
        alert.setTransaction(transaction);
        alert.setDescription(description);
        alert.setType(type);
        alertRepository.save(alert);
        return alert;
    }
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByTransactionId(Long transactionId, Pageable pageable) {
        try{
            Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
            if(optionalTransaction.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }

            Page<Alert> alerts = alertRepository.findByTransactionId(transactionId, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByTransactionIdAndType(Long transactionId, AlertType type, Pageable pageable) {
        try{
            Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
            if(optionalTransaction.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByTransactionIdAndType(transactionId, type, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByTransactionIdAndStatus(Long transactionId, AlertStatus status, Pageable pageable){
        try{
            Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
            if(optionalTransaction.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByTransactionIdAndStatus(transactionId, status, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByTransactionIdAndTypeAndStatus(Long transactionId, AlertType type, AlertStatus status, Pageable pageable) {
        try{
            Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
            if(optionalTransaction.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByTransactionIdAndTypeAndStatus(transactionId, type, status, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByAccountId(Long accountId, Pageable pageable) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByAccountId(accountId, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByAccountIdAndType(Long accountId, AlertType type, Pageable pageable) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByAccountIdAndType(accountId, type, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByAccountIdAndStatus(Long accountId, AlertStatus status, Pageable pageable) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByAccountIdAndStatus(accountId, status, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> getByAccountIdAndTypeAndStatus(Long accountId, AlertType type, AlertStatus status, Pageable pageable) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Alert> alerts = alertRepository.findByAccountIdAndTypeAndStatus(accountId, type, status, pageable);
            return new ApiResponse<>(alerts.map(this::toAlertDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> search(AlertSearchDTO alertSearchDTO, Pageable pageable) {
        try{
            Specification<Alert> spec = AlertSpecification.hasTransaction(alertSearchDTO.getTransactionId())
                    .and(AlertSpecification.hasStatus(alertSearchDTO.getStatus()))
                    .and(AlertSpecification.hasType(alertSearchDTO.getType()))
                    .and(AlertSpecification.createdAfter(alertSearchDTO.getStart()))
                    .and(AlertSpecification.createdBefore(alertSearchDTO.getEnd()));
            return new ApiResponse<>(alertRepository.findAll(spec, pageable), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> selfSearch(Long id, AlertUserSearchDTO alertUserSearchDTO, Pageable pageable) {
        try{
            Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
            if(optionalTransaction.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Specification<Alert> spec = AlertSpecification.hasTransaction(id)
                    .and(AlertSpecification.hasStatus(alertUserSearchDTO.getStatus()))
                    .and(AlertSpecification.hasType(alertUserSearchDTO.getType()))
                    .and(AlertSpecification.createdAfter(alertUserSearchDTO.getStart()))
                    .and(AlertSpecification.createdBefore(alertUserSearchDTO.getEnd()));
            return new ApiResponse<>(alertRepository.findAll(spec, pageable), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
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
