package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.*;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    ApiResponse<Object> createCustomer(CustomerCreateDTO customerCreateDTO);
    ApiResponse<Object> updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO);
    ApiResponse<Object> getAll(Pageable pageable);
    ApiResponse<Object> getCustomerById(Long id);
    ApiResponse<Object> searchCustomers(CustomerSearchDTO dto, Pageable pageable);

}