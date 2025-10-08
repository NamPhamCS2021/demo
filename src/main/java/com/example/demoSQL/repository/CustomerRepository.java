package com.example.demoSQL.repository;

import com.example.demoSQL.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmailAndId(String Email, Long Id);
    boolean existsByPhoneNumberAndId(String phoneNumber, Long Id);
    boolean existsByPhoneNumberOrEmail(String phoneNumber, String email);
    @Query("SELECT c FROM Customer c WHERE c.publicId = :publicId")
    Optional<Customer> findByPublicId(UUID publicId);
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findByEmail(String email);
}
