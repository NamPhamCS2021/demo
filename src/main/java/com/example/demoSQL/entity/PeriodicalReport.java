package com.example.demoSQL.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "periodical_report")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicalReport {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long numberOfTransactions;

    @Column
    private BigDecimal totalAmount;

    @Column
    private BigDecimal averageAmount;

    @Column
    private BigDecimal maximumAmount;

    @Column
    private BigDecimal minimumAmount;

    @Column
    private LocalDateTime timestamp;

    @Column
    private LocalDateTime startAt;

   @Column
   private LocalDateTime endAt;

   @PrePersist
   private void initialisePeriodicalReport() {
        this.timestamp = LocalDateTime.now();
   }
}
