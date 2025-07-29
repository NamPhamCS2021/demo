package com.example.demoSQL.repository;

import com.example.demoSQL.entity.PeriodicalPayment;
import com.example.demoSQL.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodicalPaymentRepository extends JpaRepository<PeriodicalPayment, Long>, JpaSpecificationExecutor<PeriodicalPayment> {
    @Query("SELECT p from PeriodicalPayment p WHERE p.account.id = :accountId")
    Page<PeriodicalPayment> findByAccountId(Long accountId, Pageable pageable);
    @Query("SELECT p from PeriodicalPayment p WHERE p.account.id = :accountId AND p.status = :status")
    Page<PeriodicalPayment> findByAccountIdAndStatus(Long accountId, SubscriptionStatus status, Pageable pageable);
}
