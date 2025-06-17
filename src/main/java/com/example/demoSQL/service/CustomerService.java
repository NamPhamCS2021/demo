package com.example.demoSQL.service;

import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerResponseDTO;
import com.example.demoSQL.dto.customer.CustomerSummaryDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerCreateDTO customerCreateDTO);
    CustomerResponseDTO updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO);
    CustomerResponseDTO getCustomerById(Long id);
    Page<CustomerSummaryDTO> getAll(Pageable pageable);
}