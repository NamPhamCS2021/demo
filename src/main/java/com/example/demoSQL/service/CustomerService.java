package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerResponseDTO;
import com.example.demoSQL.dto.customer.CustomerSummaryDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    ApiResponse<Object> createCustomer(CustomerCreateDTO customerCreateDTO);
    ApiResponse<Object> updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO);
    ApiResponse<Object> getCustomerById(Long id);
    ApiResponse<Object> getAll(Pageable pageable);
}