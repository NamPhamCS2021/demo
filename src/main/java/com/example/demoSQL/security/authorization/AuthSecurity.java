package com.example.demoSQL.security.authorization;

import com.example.demoSQL.repository.*;
import com.example.demoSQL.security.repository.UserRepository;
import com.example.demoSQL.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authSecurity")
@RequiredArgsConstructor
public class AuthSecurity {

    private final CustomerRepository customerRepository;

    private final AccountRepository accountRepository;

    private final PeriodicallyPaymentRepository periodicallyPaymentRepository;

    private final TransactionRepository transactionRepository;

    private final AccountStatusHistoryRepository accountStatusHistoryRepository;

    private final AlertRepository alertRepository;

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private String getCurrentEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isSelfCustomer(Long customerId) {
        if(isAdmin()) {
            return true;
        }
        return customerRepository.findById(customerId)
                .map(c -> c.getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }

    public boolean isOwnerOfAccount(Long accountId) {
        if(isAdmin()) {
            return true;
        }
        return accountRepository.findById(accountId)
                .map(a -> a.getCustomer().getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }

    public boolean isOwnerOfPayment(Long paymentId) {
        if(isAdmin()) {
            return true;
        }
        return periodicallyPaymentRepository.findById(paymentId)
                .map(p -> p.getAccount().getCustomer().getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }

    public boolean isOwnerOfTransaction(Long userId) {
        if(isAdmin()) {
            return true;
        }
        return transactionRepository.findById(userId)
                .map(t -> t.getAccount().getCustomer().getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }
    public boolean isOwnerOfAccountStatusHistory(Long userId) {
        if(isAdmin()) {
            return true;
        }
        return accountStatusHistoryRepository.findById(userId)
                .map(a -> a.getAccount().getCustomer().getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }

    public boolean isOwnerOfAlert(Long userId) {
        if(isAdmin()) {
            return true;
        }
        return alertRepository.findById(userId)
                .map(alert -> alert.getTransaction().getAccount().getCustomer().getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }
}
