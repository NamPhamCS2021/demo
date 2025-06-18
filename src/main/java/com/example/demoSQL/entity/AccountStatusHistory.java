package com.example.demoSQL.entity;

import com.example.demoSQL.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_status_history")
public class AccountStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    @Column(nullable = false)
    private AccountStatus status;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void initialiseAccountStatusHistory() {
        this.timestamp = LocalDateTime.now();
    }

}
