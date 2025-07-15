package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
            Account account = accountRepository.findById(transactionCreateDTO.getAccountId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getAccountId()+" not found"));


            if(account.getStatus() != AccountStatus.ACTIVE){
                return new ApiResponse<>(ReturnMessage.INACTIVE.getCode(), ReturnMessage.INACTIVE.getMessage());
            }

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(transactionCreateDTO.getAmount());
            transaction.setType(TransactionType.DEPOSIT);
            transaction.setLocation(transactionCreateDTO.getLocation());

            account.setBalance(account.getBalance().add(transactionCreateDTO.getAmount()));
            transactionRepository.save(transaction);
            return new ApiResponse<>(toTransactionResponseDTO(transaction), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch(EntityNotFoundException e) {
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());

        } catch(Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @CachePut(value = "transactionsByAccount", key = "#transactionCreateDTO.accountId")
    public ApiResponse<Object> withdraw(TransactionCreateDTO transactionCreateDTO) {
        try{
            Account account = accountRepository.findById(transactionCreateDTO.getAccountId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getAccountId()+" not found"));

            if(account.getStatus() != AccountStatus.ACTIVE){
                return new ApiResponse<>(ReturnMessage.INACTIVE.getCode(), ReturnMessage.INACTIVE.getMessage());
            }

            if(account.getBalance().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(ReturnMessage.INSUFFICIENT_BALANCE.getCode(), ReturnMessage.INSUFFICIENT_BALANCE.getMessage());
            }
            if(account.getAccountLimit().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(ReturnMessage.OFF_LIMIT.getCode(), ReturnMessage.OFF_LIMIT.getMessage());
            }

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(transactionCreateDTO.getAmount());
            transaction.setType(TransactionType.WITHDRAWAL);
            transaction.setLocation(transactionCreateDTO.getLocation());

            account.setBalance(account.getBalance().subtract(transactionCreateDTO.getAmount()));
            transactionRepository.save(transaction);
            return new ApiResponse<>(toTransactionResponseDTO(transaction), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @CachePut(value = "transactionsByAccount", key = "#transactionCreateDTO.accountId")
    public ApiResponse<Object> transfer(TransactionCreateDTO transactionCreateDTO){
        try{
            Account account = accountRepository.findById(transactionCreateDTO.getAccountId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getAccountId()+" not found"));
            Account receiver = accountRepository.findById(transactionCreateDTO.getReceiverId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getReceiverId()+" not found"));

            if(account.getBalance().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(ReturnMessage.INSUFFICIENT_BALANCE.getCode(), ReturnMessage.INSUFFICIENT_BALANCE.getMessage());
            }
            if(account.getStatus() != AccountStatus.ACTIVE){
                return new ApiResponse<>(ReturnMessage.INACTIVE.getCode(), ReturnMessage.INACTIVE.getMessage());
            }

            if(account.getAccountLimit().compareTo(transactionCreateDTO.getAmount()) < 0){
                return new ApiResponse<>(ReturnMessage.OFF_LIMIT.getCode(), ReturnMessage.OFF_LIMIT.getMessage());
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
            return new ApiResponse<>(toTransactionResponseDTO(transaction), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#id")
    public ApiResponse<Object> getTransaction(Long id){
        try{
            Transaction transaction = transactionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Transaction with id "+id+" not found"));
            return new ApiResponse<>(toTransactionResponseDTO(transaction), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionsByAccount", key = "#accountId")
    public ApiResponse<Object> getTransactionsByAccountId(Long accountId, Pageable pageable)
    {
        try{
            Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
            return new ApiResponse<>(transactions.map(this::toTransactionResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionsByType", key = "#type")
    public ApiResponse<Object> getTransactionsByType(TransactionType type, Pageable pageable)
    {
        try{
            Page<Transaction> transactions = transactionRepository.findByType(type, pageable);
            return new ApiResponse<>(transactions.map(this::toTransactionResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionsByAccountAndType", key = "#accountId + '-' + #type")
    public ApiResponse<Object> getTransactionsByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable)
    {
        try{
            Page<Transaction> transactions = transactionRepository.findByAccountIdAndType(accountId, type, pageable);
            return new ApiResponse<>(transactions.map(this::toTransactionResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> countTransactionsByLocation(){
        try{
            List<LocationCount> locationCounts = transactionRepository.countTransactionsByLocation();
            return new ApiResponse<>(locationCounts, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
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
