package com.example.demoSQL.repository;

import com.example.demoSQL.Utils.LocationCount;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);
    @Query("Select t FROM Transaction t WHERE t.account.id = :accountId OR t.receiver.id = :accountId")
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
    Page<Transaction> findByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable);
    //Page<Transaction> findByReceiverId(Long receiverId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.timestamp BETWEEN :start AND :end")
    Page<Transaction> findBetweenTimeByAccount(Long accountId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber AND t.timestamp BETWEEN :start AND :end")
    Page<Transaction> findBetweenTimeByAccountNumber(String accountNumber, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT t.location AS location, COUNT(t) AS count FROM Transaction t GROUP BY t.location")
    List<LocationCount> countTransactionsByLocation();
}
