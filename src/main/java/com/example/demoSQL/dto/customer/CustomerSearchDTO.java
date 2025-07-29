package com.example.demoSQL.dto.customer;

import com.example.demoSQL.enums.CustomerType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CustomerSearchDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private CustomerType type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime to;
}
