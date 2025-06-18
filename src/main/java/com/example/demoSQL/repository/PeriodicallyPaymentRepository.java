package com.example.demoSQL.repository;

import com.example.demoSQL.entity.PeriodicallyPayment;
import com.example.demoSQL.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodicallyPaymentRepository extends JpaRepository<PeriodicallyPayment, Long> {
    @Query("SELECT p from PeriodicallyPayment p WHERE p.account.id = :accountId")
    Page<PeriodicallyPayment> findByAccountId(Long accountId, Pageable pageable);
    @Query("SELECT p from PeriodicallyPayment p WHERE p.account.id = :accountID AND p.status = :status")
    Page<PeriodicallyPayment> findByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable);
}
