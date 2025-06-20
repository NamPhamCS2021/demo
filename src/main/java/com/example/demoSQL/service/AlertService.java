package com.example.demoSQL.service;

import com.example.demoSQL.dto.alert.AlertDTO;
import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertService {
    void detectAbnormalTransactions();
    Page<AlertDTO> getAll(Pageable pageable);
    Page<AlertDTO> getByTransactionId(Long transactionId, Pageable pageable);
    Page<AlertDTO> getByTransactionIdAndType(Long transactionId, AlertType type, Pageable pageable);
    Page<AlertDTO> getByTransactionIdAndStatus(Long transactionId, AlertStatus status, Pageable pageable);
    Page<AlertDTO> getByTransactionIdAndTypeAndStatus(Long transactionId, AlertType type, AlertStatus status, Pageable pageable);
    Page<AlertDTO> getByAccountId(Long accountId, Pageable pageable);
    Page<AlertDTO> getByAccountIdAndType(Long accountId, AlertType type, Pageable pageable);
    Page<AlertDTO> getByAccountIdAndStatus(Long accountId, AlertStatus status, Pageable pageable);
    Page<AlertDTO> getByAccountIdAndTypeAndStatus(Long accountId, AlertType type, AlertStatus status, Pageable pageable);
}
