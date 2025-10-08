package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.*;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    ApiResponse<Object> getCustomerByEmail(String email);
    ApiResponse<Object> createCustomer(CustomerCreateDTO customerCreateDTO);
    ApiResponse<Object> updateCustomer(UUID publicId, CustomerUpdateDTO customerUpdateDTO);
    ApiResponse<Object> getAll(Pageable pageable);
    ApiResponse<Object> getCustomerByPublicId(UUID publicId);
    ApiResponse<Object> search(CustomerSearchDTO dto, Pageable pageable);

}