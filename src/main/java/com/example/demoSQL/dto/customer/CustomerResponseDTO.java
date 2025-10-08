package com.example.demoSQL.dto.customer;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.CustomerType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private UUID publicId;
    private String firstName;
    private String lastName;
    private String email;
    private CustomerType type;
    private String phoneNumber;
    private LocalDateTime createDate;
    private List<Account> accounts;

}
