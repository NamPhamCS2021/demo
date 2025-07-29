package com.example.demoSQL.security.authorization;

import com.example.demoSQL.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authSecurity")
@RequiredArgsConstructor
public class AuthSecurity {

    private final CustomerRepository customerRepository;

    private final AccountRepository accountRepository;

    private final PeriodicalPaymentRepository periodicalPaymentRepository;

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
        return periodicalPaymentRepository.findById(paymentId)
                .map(p -> p.getAccount().getCustomer().getEmail().equals(getCurrentEmail()))
                .orElse(false);
    }

    public boolean isOwnerOfTransaction(Long transactionId) {
        if(isAdmin()) {
            return true;
        }
        return transactionRepository.findById(transactionId)
                .map(t -> {
                    String currentEmail = getCurrentEmail();
                    String senderMail = t.getAccount().getCustomer().getEmail();
                    String rerceiverMail = t.getReceiver() != null ? t.getReceiver().getCustomer().getEmail() : null;
                    return currentEmail.equals(senderMail) || currentEmail.equals(rerceiverMail);
                })
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
