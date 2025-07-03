package com.example.demoSQL.repository;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    @Query("SELECT COUNT(a) > 0 FROM Alert a WHERE a.transaction.account.id = :accountId")
    Boolean existsByAccountId(Long accountId);
    Page<Alert> findByTransactionId(Long transactionId, Pageable pageable);
    Page<Alert> findByTransactionIdAndType(Long transactionId, AlertType type, Pageable pageable);
    Page<Alert> findByTransactionIdAndStatus(Long transactionId, AlertStatus status, Pageable pageable);
    Page<Alert> findByTransactionIdAndTypeAndStatus(Long transactionId, AlertType type, AlertStatus status, Pageable pageable);
    @Query("SELECT a FROM Alert a WHERE a.transaction.account.id = :accountId OR a.transaction.receiver.id = :accountId")
    Page<Alert> findByAccountId(Long accountId, Pageable pageable);
    @Query("SELECT a FROM Alert a WHERE (a.transaction.account.id = :accountId OR a.transaction.receiver.id = :accountId) AND a.type = :type")
    Page<Alert> findByAccountIdAndType(Long accountId, AlertType type, Pageable pageable);
    @Query("SELECT a FROM Alert a WHERE (a.transaction.account.id = :accountId OR a.transaction.receiver.id = :accountId) AND a.status = :status")
    Page<Alert> findByAccountIdAndStatus(Long accountId, AlertStatus status, Pageable pageable);
    @Query("SELECT a FROM Alert a WHERE (a.transaction.account.id = :accountId OR a.transaction.receiver.id = :accountId) AND a.type = :type AND a.status = :status")
    Page<Alert> findByAccountIdAndTypeAndStatus(Long accountId, AlertType type, AlertStatus status, Pageable pageable);
    @Query("SELECT a.transaction.account FROM Alert a GROUP BY a.transaction.account.id ORDER BY Count(a) DESC")
    Account getMostAlertAccount();
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.transaction.account.id = :accountId")
    Long countAllAlertsByAccountId(Long accountId);
}
