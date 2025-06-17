package com.example.demoSQL.dto.customer;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerSummaryDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
