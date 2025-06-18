package com.example.demoSQL.repository;

import com.example.demoSQL.entity.AccountStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, Long> {
    boolean existsByAccountId(Long accountId);
    @Query("SELECT a FROM AccountStatusHistory a WHERE a.account.id = :accountId")
    Page<AccountStatusHistory> findByAccountId(Long accountId, Pageable pageable);
    @Query("SELECT a FROM AccountStatusHistory a WHERE a.account.accountNumber = :accountNumber")
    Page<AccountStatusHistory> findByAccountNumber(String accountNumber, Pageable pageable);
    @Query("SELECT a FROM AccountStatusHistory a WHERE (a.account.id = :accountId ) AND (a.timestamp BETWEEN :start AND :end)")
    Page<AccountStatusHistory> findBetweenByAccountId(Long accountId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
    @Query("SELECT a FROM AccountStatusHistory a WHERE (a.account.accountNumber = :accountNumber ) AND (a.timestamp BETWEEN :start AND :end)")
    Page<AccountStatusHistory> findBetweenByAccountNumber(String accountNumber, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

}
