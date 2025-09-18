package com.example.demoSQL.repository;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Page<Account> findByCustomerId(Long customerId, Pageable pageable);
    Page<Account> findByStatus(AccountStatus status, Pageable pageable);
    Page<Account> findByCustomerIdAndStatus(Long customerId, AccountStatus status, Pageable pageable);
    @Query("SELECT Count(a) FROM Account a")
    Long countAllAccounts();
    @Query("SELECT Count(a) FROM Account a WHERE a.status = :status")
    Long countAllAccountsByStatus(@Param("status") AccountStatus status);
    @Query("SELECT Count(a) FROM Account a WHERE a.customer.type = :type")
    Long countAllAccountsByType(@Param("type") CustomerType type);
    @Query("SELECT a FROM Account a JOIN FETCH a.customer WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumber(String accountNumber);
    @Query("SELECT a FROM Account  a WHERE a.customer.email = :email")
    Page<Account> findAccountByEmail(String email, Pageable pageable);
}
