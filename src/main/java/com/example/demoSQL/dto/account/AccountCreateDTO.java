package com.example.demoSQL.dto.account;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateDTO {
    private Long customerId;
}
