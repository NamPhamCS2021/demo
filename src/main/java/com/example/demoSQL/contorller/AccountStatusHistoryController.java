package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;

import com.example.demoSQL.service.AccountStatusHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/accountstatushistory")
@Validated
@RequiredArgsConstructor
public class AccountStatusHistoryController {

    private final AccountStatusHistoryService accountStatusHistoryService;



    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @GetMapping("/account/{id}")
    public ResponseEntity<Page<AccountStatusHistoryResponseDTO>> getAccountStatusHistoryById(@PathVariable Long id,
                                                                                             @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<AccountStatusHistoryResponseDTO> accountStatusHistoryPage = accountStatusHistoryService.findByAccountId(id, pageable);
        return ResponseEntity.ok(accountStatusHistoryPage);
    }


    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @GetMapping("account/between")
    public ResponseEntity<Page<AccountStatusHistoryResponseDTO>>getAccountStatusHistoryByIdAndStatus(@RequestParam Long id,
                                                                                                     @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                                                                     @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end,
                                                                                                     @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AccountStatusHistoryResponseDTO> accountStatusHistoryPage = accountStatusHistoryService.findBetweenByAccount(id, start, end, Pageable.unpaged());
        return ResponseEntity.ok(accountStatusHistoryPage);
    }


}
