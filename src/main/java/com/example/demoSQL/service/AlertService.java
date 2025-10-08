package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.alert.AlertDTO;
import com.example.demoSQL.dto.alert.AlertSearchDTO;
import com.example.demoSQL.dto.alert.AlertUserSearchDTO;
import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AlertService {
    void detectAbnormalTransactions();
    ApiResponse<Object> getAll(Pageable pageable);
    ApiResponse<Object> getByTransactionId(UUID transactionPublicId, Pageable pageable);
    ApiResponse<Object> getByAccountNumber(String accountNumber, Pageable pageable);
    ApiResponse<Object> search(AlertSearchDTO dto, Pageable pageable);
    ApiResponse<Object> selfSearch(UUID publicTransactionId, AlertUserSearchDTO dto, Pageable pageable);
}
