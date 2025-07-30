package com.example.demoSQL.dto.customer;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.CustomerType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private CustomerType type;
    private String phoneNumber;
    private List<Account> accounts;

}
