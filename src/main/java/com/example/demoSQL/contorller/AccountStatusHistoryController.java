package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;

import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistorySearchDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryUserSearchDTO;
import com.example.demoSQL.service.AccountStatusHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/accountstatushistory")
@Validated
@RequiredArgsConstructor
public class AccountStatusHistoryController {

    private final AccountStatusHistoryService accountStatusHistoryService;



    @GetMapping("/account/{id}")
    public ApiResponse<Object> getAccountStatusHistoryById(@PathVariable Long id,
                                                   @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return accountStatusHistoryService.findByAccountId(id, pageable);
    }


    @GetMapping("account/between")
    public ApiResponse<Object> getAccountStatusHistoryByIdAndStatus(@RequestParam Long id,
                                                                    @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                                    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end,
                                                                    @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountStatusHistoryService.findBetweenByAccount(id, start, end, pageable);
    }

    @PostMapping("/search")
    public ApiResponse<Object> searchAccountStatusHistory(@Valid @RequestBody AccountStatusHistorySearchDTO dto,
                                                          @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountStatusHistoryService.search(dto, pageable);
    }

    @PostMapping("/search/{id}")
    public ApiResponse<Object> selfSearch(@PathVariable Long id, @Valid @RequestBody AccountStatusHistoryUserSearchDTO dto,
                                          @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountStatusHistoryService.selfSearch(id, dto, pageable);
    }


}
