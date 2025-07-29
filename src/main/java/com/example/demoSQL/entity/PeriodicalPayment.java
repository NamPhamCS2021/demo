package com.example.demoSQL.entity;


import com.example.demoSQL.enums.Period;
import com.example.demoSQL.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "periodical_payment")
public class PeriodicalPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private Period period;;

    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column
    private LocalDateTime endedAt;

    @JoinColumn(name = "account_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @PrePersist
    public void initialisePeriodicallyPayment() {
        this.status = SubscriptionStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();

        switch(this.period) {
            case WEEKLY -> this.endedAt = this.startedAt.plusWeeks(1);
            case MONTHLY -> this.endedAt = this.startedAt.plusMonths(1);
            case YEARLY -> this.endedAt = this.startedAt.plusYears(1);
        }
    }

}
