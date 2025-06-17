package com.example.demoSQL.dto.customer;

import jakarta.validation.constraints.Email;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateDTO {


    private String phoneNumber;

    @Email(message = "Please provide a valid email")
    private String email;
}
