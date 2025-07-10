package com.example.demoSQL.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EResponseCode {
    SUCCESS("00", "Success"),
    FAIL("01", "Fail"),
    UNAUTHORIZED("02", "Unauthorized"),
    NOT_FOUND("03", "NOT_FOUND"),
    INACTIVE("04", "Account is not active"),
    INSUFFICIENT_BALANCE("05", "Mot enough balance"),
    OFF_LIMIT("06", "Limit exceeded"),
    ALREADY_EXISTED("07", "Mail or phone is already registered");
    private final String code;
    private final String message;
}
