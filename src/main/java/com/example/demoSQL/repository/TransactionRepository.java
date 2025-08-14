package com.example.demoSQL.repository;

import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByAccountId(Long accountId);
    @Query("Select t FROM Transaction t WHERE t.account.id = :accountId OR t.receiver.id = :accountId")
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
    Page<Transaction> findByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable);
    //Page<Transaction> findByReceiverId(Long receiverId, Pageable pageable);
    @Query("SELECT t.location AS location, COUNT(t) AS count FROM Transaction t GROUP BY t.location")
    List<LocationCount> countTransactionsByLocation();
    @Query("SELECT t FROM Transaction t WHERE t.checked = false")
    List<Transaction> findByCheckedFalse();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.timestamp BETWEEN :start AND :end")
    BigDecimal getTotalTransactionAmountBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.timestamp BETWEEN :start and :end")
    BigDecimal getAvgTransactionAmountBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.timestamp BETWEEN :startDate AND :endDate")
    Long countAllTransactionByDate(LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT t from Transaction t WHERE t.timestamp BETWEEN :start AND :end")
    List<Transaction> findBetweenTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT MAX(t.amount) from Transaction t WHERE t.timestamp BETWEEN :start AND :end")
    Transaction findBiggestTransactionBetweenTime (LocalDateTime start, LocalDateTime end);

    @Query("SELECT MIN(t.amount) from Transaction t WHERE t.timestamp BETWEEN :start AND :end")
    Transaction findSmallestTransactionBetweenTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.timestamp BETWEEN :start AND :end")
    List<Transaction> findBetweenTimeByAccount(Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.account.id = :accountId OR t.receiver.id = :accountId) AND t.timestamp BETWEEN :start AND :end")
    Long countTransactionBetweenTimeByAccount(Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.receiver.id = :receiverId AND t.timestamp BETWEEN :start AND :end")
    Long countTransactionBetweenTimeByReceiver(Long receiverId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT Count(t) FROM Transaction t WHERE t.account.accountNumber = :accountNumber AND t.timestamp BETWEEN :start AND :end")
    Long countBetweenTimeByAccountNumber(String accountNumber, LocalDateTime start, LocalDateTime end);



    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.account.id = :accountId OR t.receiver.id = :accountId) AND (t.timestamp BETWEEN :start AND :end)")
    Long countBetweenTimeByAccountId(@Param("accountId") Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT MAX(t.amount) FROM Transaction t WHERE (t.account.id = :accountId) AND (t.timestamp BETWEEN :start AND :end)")
    BigDecimal findBiggestTransactionBetweenTimeByAccountId(@Param("accountId") Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT MIN(t.amount) FROM Transaction t WHERE (t.account.id = :accountId OR t.receiver.id = :accountId) AND (t.timestamp BETWEEN :start AND :end)")
    BigDecimal findSmallestTransactionBetweenTimeByAccountId(@Param("accountId") Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE (t.account.id = :accountId OR t.receiver.id = :accountId) AND (t.timestamp BETWEEN :start AND :end)")
    BigDecimal findAverageTransactionBetweenTimeByAccountId(@Param("accountId") Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.amount) from Transaction t WHERE (t.account.id = :accountId OR t.receiver.id = :accountId) AND (t.timestamp BETWEEN :start AND :END)")
    BigDecimal findTotalTransactionAmountByAccountId(@Param("accountId") Long accountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE (t.account.id = :accountId OR t.receiver.id = :accountId) AND (t.timestamp BETWEEN :start AND :end)")
    List<Transaction> findBetweenTimeByAccountIdAndType(@Param("accountId") Long accountId, LocalDateTime start, LocalDateTime end, TransactionType type);
}
