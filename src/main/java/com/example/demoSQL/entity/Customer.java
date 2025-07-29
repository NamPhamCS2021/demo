package com.example.demoSQL.entity;


import com.example.demoSQL.enums.CustomerType;
import com.example.demoSQL.security.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private CustomerType type;

    @CreatedDate
    @Column(name = "created_date",nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "customer", cascade =CascadeType.ALL, fetch =FetchType.LAZY)
    private List<Account> accounts;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void initialiseCustomer() {
        this.createdDate = LocalDateTime.now();
    }
}
