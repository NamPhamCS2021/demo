package com.example.demoSQL.dto.transaction;

import com.example.demoSQL.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreateDTO {

    public interface OnWithdraw{}
    public interface OnTransfer{}

    @NotNull(groups = {OnWithdraw.class, OnTransfer.class})
    private String accountNumber;
    @NotNull(groups = {OnTransfer.class})
    private String receiverNumber;
    @PositiveOrZero
    @NotNull(groups = {OnWithdraw.class, OnTransfer.class})
    private BigDecimal amount;
    @NotNull(groups = {OnWithdraw.class, OnTransfer.class})
    private String location;
}
