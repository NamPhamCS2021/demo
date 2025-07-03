package com.example.demoSQL.service;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.CustomerType;
import com.example.demoSQL.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ReportServiceImpl implements ReportService{

    @Autowired
    private AccountStatusHistoryRepository accountStatusHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    AccountStatusHistoryRepository accountStatusHistoryRepository1;

    @Autowired
    private AlertRepository alertRepository;



    @Override
    public Map<String, Object> generateReport(LocalDateTime start, LocalDateTime end) {
        BigDecimal biggestTransactionAmount = transactionRepository.findBiggestTransactionBetweenTime(start, end).getAmount();
        BigDecimal smallestTrasnactionAmount = transactionRepository.findSmallestTransactionBetweenTime(start, end).getAmount();
        Long totalTransaction = transactionRepository.countAllTransactionByDate(start, end);
        BigDecimal averageTransactionAmount = transactionRepository.getAvgTransactionAmountBetween(start, end);
        BigDecimal totalAmount = transactionRepository.getTotalTransactionAmountBetween(start, end);

        Map<String, Object> report = new HashMap<>();
        report.put("biggest transaction amount", biggestTransactionAmount);
        report.put("smallest trasnaction amount", smallestTrasnactionAmount);
        report.put("total transactions made", totalTransaction);
        report.put("average transaction amount", averageTransactionAmount);
        report.put("total transaction amount", totalAmount);

        return report;
    }

    @Override
    public Map<String, Object> generateReportByAccount(Long accountId, LocalDateTime start, LocalDateTime end) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("Can not find account with id as: " +accountId));
        String customerName = account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName();
        String accountNumber = account.getAccountNumber();
        BigDecimal balance = account.getBalance();
        BigDecimal accountLimit = account.getAccountLimit();
        BigDecimal biggestAmount = transactionRepository.findBiggestTransactionBetweenTimeByAccountId(accountId,start,end);
        BigDecimal smallestAmount = transactionRepository.findSmallestTransactionBetweenTimeByAccountId(accountId,start,end);
        Long totalTransaction = transactionRepository.countTransactionBetweenTimeByAccount(accountId, start, end);
        BigDecimal averageTransactionAmount = transactionRepository.findAverageTransactionBetweenTimeByAccountId(accountId, start, end);
        BigDecimal totalAmount = transactionRepository.findTotalTransactionAmountByAccountId(accountId, start, end);
        Long totalAlert = alertRepository.countAllAlertsByAccountId(accountId);

        Map<String, Object> report = new HashMap<>();
        report.put("customer name", customerName);
        report.put("account number", accountNumber);
        report.put("balance", balance);
        report.put("account limit", accountLimit);
        report.put("biggest transaction amount", biggestAmount);
        report.put("smallest trasnaction amount", smallestAmount);
        report.put("total transactions made", totalTransaction);

        return report;
    }

    @Override
    public Map<String, Object> generateAccountReport(LocalDateTime start, LocalDateTime end) {
        Long totalAccount = accountRepository.countAllAccounts();
        Long corporalAccounts = accountRepository.countAllAccountsByType(CustomerType.CORPORAL);
        Long personalAccounts = accountRepository.countAllAccountsByType(CustomerType.PERSONAL);
        Long temporaryAccounts = accountRepository.countAllAccountsByType(CustomerType.TEMPORARY);
        Long activeAccount = accountRepository.countAllAccountsByStatus(AccountStatus.ACTIVE);

        Map<String, Object> report = new HashMap<>();
        report.put("total accounts", totalAccount);
        report.put("corporal accounts", corporalAccounts);
        report.put("personal accounts", personalAccounts);
        report.put("temporary accounts", temporaryAccounts);
        report.put("active accounts", activeAccount);

        return report;
    }

}
