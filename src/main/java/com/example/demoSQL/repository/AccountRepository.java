package com.example.demoSQL.repository;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Long customerId);
    Page<Account> findByCustomerId(Long customerId, Pageable pageable);
    Page<Account> findByStatus(AccountStatus status, Pageable pageable);
    Page<Account> findByCustomerIdAndStatus(Long customerId, AccountStatus status, Pageable pageable);
    @Query("SELECT Count(a) FROM Account a")
    Long countAllAccounts();
    @Query("SELECT Count(a) FROM Account a WHERE a.status = :status")
    Long countAllAccountsByStatus(AccountStatus status);
    @Query("SELECT Coung(a) FROM Account a WHERE a.customer.type = :type")
    Long countAllAccountsByType(CustomerType type);
}
