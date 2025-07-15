package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import com.example.demoSQL.service.AlertService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/alerts")
@RestController
public class AlertController {

    public final AlertService alertService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ApiResponse<Object> getAll(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        ApiResponse<Object> apiResponse = alertService.getAll(pageable);
        return apiResponse;
    }

    @PreAuthorize("@authSecurity.isOwnerOfTransaction(#id)")
    @GetMapping("/transaction/id/{id}")
    public ApiResponse<Object> getByTransactionId(@PathVariable Long id,
                                                  @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
         return alertService.getByTransactionId(id, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfTransaction(#id)")
    @GetMapping("/transaction/{id}/status/{status}")
    public ApiResponse<Object> getByTransactionIdAndStatus(@PathVariable Long id, @PathVariable AlertStatus status,
                                                           @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByTransactionIdAndStatus(id,status, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfTransaction(#id)")
    @GetMapping("/transaction/{id}/type/{type}")
    public ApiResponse<Object> getByTransactionIdAndType(@PathVariable Long id, @PathVariable AlertType type,
                                                         @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByTransactionIdAndType(id, type, pageable);
    }

    @PreAuthorize("authSecurity.isOwnerOfTransaction(#id)")
    @GetMapping("/transaction/{id}/type/{type}/status/{status}")
    public ApiResponse<Object> getByTransactionIdAndTypeAndStatus(@PathVariable Long id, @PathVariable AlertType type,
                                                                  @PathVariable AlertStatus status,
                                                                  @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByTransactionIdAndTypeAndStatus(id, type, status, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/{id}")
    public ApiResponse<Object> getByAccountId(@PathVariable Long id,
                                              @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByAccountId(id, pageable);

    }

    @PreAuthorize("authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/{id}/type/{type}")
    public ApiResponse<Object> getByAccountIdAndType(@PathVariable Long id, @PathVariable AlertType type,
                                                     @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByAccountIdAndType(id, type, pageable);
    }
    @PreAuthorize("authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/{id}/status/{status}")
    public ApiResponse<Object> getByAccountIdAndStatus(@PathVariable Long id, @PathVariable AlertStatus status,
                                                       @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByAccountIdAndStatus(id, status, pageable);
    }

    @PreAuthorize("authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/account/{id}/type/{type}/status/{status}")
    public ApiResponse<Object> getByAccountIdAndTypeAndStatus(@PathVariable Long id, @PathVariable AlertType type,
                                                              @PathVariable AlertStatus status,
                                                              @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return alertService.getByAccountIdAndTypeAndStatus(id, type, status, pageable);
    }
}
