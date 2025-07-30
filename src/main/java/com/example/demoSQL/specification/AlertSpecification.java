package com.example.demoSQL.specification;

import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AlertSpecification {

    public static Specification<Alert> hasTransaction(Long transactionId) {
        return ((root, query, builder) ->
                builder.equal(root.get("transaction"), transactionId));
    }
    public static Specification<Alert> hasType(AlertType type) {
        return((root, query, builder) ->
                builder.equal(root.get("type"), type));
    }

    public static Specification<Alert> hasStatus(AlertStatus status) {
        return((root, query, builder) ->
                builder.equal(root.get("status"), status));
    }

    public static Specification<Alert> createdBefore(LocalDateTime dateTime) {
        return((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("created"), dateTime));
    }

    public static Specification<Alert> createdAfter(LocalDateTime dateTime) {
        return((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("created"), dateTime));
    }


}
