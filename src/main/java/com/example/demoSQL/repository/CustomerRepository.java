package com.example.demoSQL.repository;

import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmailAndId(String Email, Long Id);
    boolean existsByPhoneNumberAndId(String phoneNumber, Long Id);
    boolean existsByPhoneNumberOrEmail(String phoneNumber, String email);
}
