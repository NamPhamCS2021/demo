package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.enums.EResponseCode;
import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    @Override
    @CachePut(value = "transactionsByAccount", key = "#transactionCreateDTO.accountId")
    public ApiResponse<Object> deposit(TransactionCreateDTO transactionCreateDTO) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(transactionCreateDTO.getAccountId());

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }

            Account account = optionalAccount.get();

            if(!account.getStatus().equals(AccountStatus.ACTIVE)){
                return new ApiResponse<>(EResponseCode.INACTIVE.getCode(), EResponseCode.INACTIVE.getMessage());
            }

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(transactionCreateDTO.getAmount());
            transaction.setType(TransactionType.DEPOSIT);
            transaction.setLocation(transactionCreateDTO.getLocation());

            account.setBalance(account.getBalance().add(transactionCreateDTO.getAmount()));
            transactionRepository.save(transaction);
            return new ApiResponse<>(toTransactionResponseDTO(transaction), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch(Exception e){
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @CachePut(value = "transactionsByAccount", key = "#transactionCreateDTO.accountId")
    public ApiResponse<Object> withdraw(TransactionCreateDTO transactionCreateDTO) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(transactionCreateDTO.getAccountId());

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }

            Account account = optionalAccount.get();

            if(account.getStatus() != AccountStatus.ACTIVE){
                return new ApiResponse<>(EResponseCode.INACTIVE.getCode(), EResponseCode.INACTIVE.getMessage());
            }

            if(account.getBalance().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(EResponseCode.INSUFFICIENT_BALANCE.getCode(), EResponseCode.INSUFFICIENT_BALANCE.getMessage());
            }
            if(account.getAccountLimit().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(EResponseCode.OFF_LIMIT.getCode(), EResponseCode.OFF_LIMIT.getMessage());
            }

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(transactionCreateDTO.getAmount());
            transaction.setType(TransactionType.WITHDRAWAL);
            transaction.setLocation(transactionCreateDTO.getLocation());

            account.setBalance(account.getBalance().subtract(transactionCreateDTO.getAmount()));
            transactionRepository.save(transaction);
            return new ApiResponse<>(toTransactionResponseDTO(transaction), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @CachePut(value = "transactionsByAccount", key = "#transactionCreateDTO.accountId")
    public ApiResponse<Object> transfer(TransactionCreateDTO transactionCreateDTO){
        try{
            Optional<Account> optionalAccount = accountRepository.findById(transactionCreateDTO.getAccountId());
            Optional<Account> optionalReceiver = accountRepository.findById(transactionCreateDTO.getReceiverId());

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            if(optionalReceiver.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }

            Account account = optionalAccount.get();
            Account receiver = optionalReceiver.get();

            if(account.getBalance().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(EResponseCode.INSUFFICIENT_BALANCE.getCode(), EResponseCode.INSUFFICIENT_BALANCE.getMessage());
            }
            if(account.getStatus() != AccountStatus.ACTIVE){
                return new ApiResponse<>(EResponseCode.INACTIVE.getCode(), EResponseCode.INACTIVE.getMessage());
            }

            if(account.getAccountLimit().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(EResponseCode.OFF_LIMIT.getCode(), EResponseCode.OFF_LIMIT.getMessage());
            }

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(transactionCreateDTO.getAmount());
            transaction.setType(TransactionType.TRANSFER);
            transaction.setReceiver(receiver);
            transaction.setLocation(transactionCreateDTO.getLocation());

            account.setBalance(account.getBalance().subtract(transactionCreateDTO.getAmount()));
            receiver.setBalance(receiver.getBalance().add(transactionCreateDTO.getAmount()));
            transactionRepository.save(transaction);
            return new ApiResponse<>(toTransactionResponseDTO(transaction), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#id")
    public ApiResponse<Object> getTransaction(Long id){
        try{
            Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
            if(optionalTransaction.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Transaction transaction = optionalTransaction.get();
            return new ApiResponse<>(toTransactionResponseDTO(transaction), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionsByAccount", key = "#accountId")
    public ApiResponse<Object> getTransactionsByAccountId(Long accountId, Pageable pageable)
    {
        try{
            Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
            return new ApiResponse<>(transactions.map(this::toTransactionResponseDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionsByType", key = "#type")
    public ApiResponse<Object> getTransactionsByType(TransactionType type, Pageable pageable)
    {
        try{
            Page<Transaction> transactions = transactionRepository.findByType(type, pageable);
            return new ApiResponse<>(transactions.map(this::toTransactionResponseDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionsByAccountAndType", key = "#accountId + '-' + #type")
    public ApiResponse<Object> getTransactionsByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable)
    {
        try{
            Page<Transaction> transactions = transactionRepository.findByAccountIdAndType(accountId, type, pageable);
            return new ApiResponse<>(transactions.map(this::toTransactionResponseDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> countTransactionsByLocation(){
        try{
            List<LocationCount> locationCounts = transactionRepository.countTransactionsByLocation();
            return new ApiResponse<>(locationCounts, EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }

    //helper
    private TransactionResponseDTO toTransactionResponseDTO(Transaction transaction){
        return TransactionResponseDTO.builder()
                .customerId(transaction.getAccount().getCustomer().getId())
                .receiverId(transaction.getReceiver() == null ? null : transaction.getReceiver().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .timestamp(transaction.getTimestamp())
                .location(transaction.getLocation())
                .build();
    }
}
