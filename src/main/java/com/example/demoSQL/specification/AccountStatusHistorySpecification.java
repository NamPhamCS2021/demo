package com.example.demoSQL.specification;

import com.example.demoSQL.entity.AccountStatusHistory;
import com.example.demoSQL.enums.AccountStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AccountStatusHistorySpecification {

    public static Specification<AccountStatusHistory> hasAccount(Long id) {
        return ((root, query, builder) ->
                builder.equal(root.get("accountStatusHistory"), id));
    }

    public static Specification<AccountStatusHistory> hasStatus(AccountStatus status) {
        return ((root, query, builder) ->
                builder.equal(root.get("accountStatusHistory"), status));
    }

    public static Specification<AccountStatusHistory> createdBefore(LocalDateTime timestamp) {
        return ((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("createdAt"), timestamp));
    }

    public static Specification<AccountStatusHistory> createdAfter(LocalDateTime timestamp) {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("createdAt"), timestamp));
    }
}
