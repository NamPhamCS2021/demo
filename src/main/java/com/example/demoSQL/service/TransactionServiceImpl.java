package com.example.demoSQL.service;

import com.example.demoSQL.Utils.LocationCount;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.Location;
import java.util.List;


@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }
    @Override
    public TransactionResponseDTO deposit(TransactionCreateDTO transactionCreateDTO) {
        Account account = accountRepository.findById(transactionCreateDTO.getAccountId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getAccountId()+" not found"));


        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account is not active");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(transactionCreateDTO.getAmount());
        transaction.setType(TransactionType.DEPOSIT);

        account.setBalance(account.getBalance().add(transactionCreateDTO.getAmount()));
        transactionRepository.save(transaction);
        return toTransactionResponseDTO(transaction);
    }

    @Override
    public TransactionResponseDTO withdraw(TransactionCreateDTO transactionCreateDTO) {
        Account account = accountRepository.findById(transactionCreateDTO.getAccountId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getAccountId()+" not found"));

        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account is not active");
        }

        if(account.getBalance().compareTo(transactionCreateDTO.getAmount()) < 0){
            throw new RuntimeException("Insufficient balance");
        }
        if(account.getAccountLimit().compareTo(transactionCreateDTO.getAmount()) < 0){
            throw new RuntimeException("Transaction limit exceeded");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(transactionCreateDTO.getAmount());
        transaction.setType(TransactionType.WITHDRAWAL);

        account.setBalance(account.getBalance().subtract(transactionCreateDTO.getAmount()));
        transactionRepository.save(transaction);
        return toTransactionResponseDTO(transaction);
    }

    @Override
    public TransactionResponseDTO transfer(TransactionCreateDTO transactionCreateDTO){
        Account account = accountRepository.findById(transactionCreateDTO.getAccountId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getAccountId()+" not found"));
        Account receiver = accountRepository.findById(transactionCreateDTO.getReceiverId()).orElseThrow(()->new EntityNotFoundException("Account with id "+transactionCreateDTO.getReceiverId()+" not found"));

        if(account.getBalance().compareTo(transactionCreateDTO.getAmount()) < 0){
            throw new RuntimeException("Insufficient balance");
        }
        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account is not active");
        }

        if(account.getAccountLimit().compareTo(transactionCreateDTO.getAmount()) < 0){
            throw new RuntimeException("Transaction limit exceeded");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(transactionCreateDTO.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setReceiver(receiver);

        account.setBalance(account.getBalance().subtract(transactionCreateDTO.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transactionCreateDTO.getAmount()));
        transactionRepository.save(transaction);
        return toTransactionResponseDTO(transaction);
    }

    @Override
    public TransactionResponseDTO getTransaction(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Transaction with id "+id+" not found"));
        return toTransactionResponseDTO(transaction);
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionsByAccountId(Long accountId, Pageable pageable)
    {
        Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
        return transactions.map(this::toTransactionResponseDTO);
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionsByType(TransactionType type, Pageable pageable)
    {
        Page<Transaction> transactions = transactionRepository.findByType(type, pageable);
        return transactions.map(this::toTransactionResponseDTO);
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionsByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable)
    {
        Page<Transaction> transactions = transactionRepository.findByAccountIdAndType(accountId, type, pageable);
        return transactions.map(this::toTransactionResponseDTO);
    }

    @Override
    public List<LocationCount> countTransactionsByLocation(){
        return transactionRepository.countTransactionsByLocation();
    }

    //helper
    private TransactionResponseDTO toTransactionResponseDTO(Transaction transaction){
        return TransactionResponseDTO.builder()
                .customerId(transaction.getAccount().getCustomer().getId())
                .receiverId(transaction.getReceiver() == null ? null : transaction.getReceiver().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
