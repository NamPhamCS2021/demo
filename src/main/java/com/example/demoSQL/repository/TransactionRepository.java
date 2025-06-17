package com.example.demoSQL.repository;

import com.example.demoSQL.entity.Transaction;
import com.example.demoSQL.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
    Page<Transaction> findByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable);
    Page<Transaction> findByReceiverId(Long receiverId, Pageable pageable);
}
