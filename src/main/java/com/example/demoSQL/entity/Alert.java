package com.example.demoSQL.entity;

import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false)
    private AlertStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @PrePersist
    public void initialiseAlert(){
        this.timestamp = LocalDateTime.now();
        this.status = AlertStatus.NEW;
    }
}
