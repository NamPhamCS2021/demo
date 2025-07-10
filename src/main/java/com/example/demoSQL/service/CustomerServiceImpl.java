package com.example.demoSQL.service;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerResponseDTO;
import com.example.demoSQL.dto.customer.CustomerSummaryDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.CustomerType;
import com.example.demoSQL.enums.EResponseCode;
import com.example.demoSQL.enums.UserRole;
import com.example.demoSQL.repository.CustomerRepository;
import com.example.demoSQL.security.entity.User;
import com.example.demoSQL.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;


    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;


    @Override
    @CachePut(value = "customers", key = "#customerCreateDTO.email")
    public ApiResponse<Object> createCustomer(CustomerCreateDTO customerCreateDTO){
        try{
            if(customerRepository.existsByEmail(customerCreateDTO.getEmail()) ||
                    customerRepository.existsByPhoneNumber(customerCreateDTO.getPhoneNumber())){
                return new ApiResponse<>(EResponseCode.ALREADY_EXISTED.getCode(), EResponseCode.ALREADY_EXISTED.getMessage());
            }

            Customer customer = new Customer();
            customer.setFirstName(customerCreateDTO.getFirstName());
            customer.setLastName(customerCreateDTO.getLastName());
            customer.setEmail(customerCreateDTO.getEmail());
            customer.setType(customerCreateDTO.getType() == null ? CustomerType.PERSONAL : customerCreateDTO.getType());
            customer.setPhoneNumber(customerCreateDTO.getPhoneNumber());

            User user = new User();
            user.setCustomer(customer);
            user.setUsername(customerCreateDTO.getEmail());
            user.setRole(UserRole.USER);
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);
            customerRepository.save(customer);

//        gotta do this later
            return new ApiResponse<>(toCustomerResponse(customer), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }


    }

    @Override
    @CachePut(value = "customers", key = "#customerUpdateDTO.email")
    public ApiResponse<Object> updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO){
        try{
            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if (optionalCustomer.isEmpty()) {
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Customer existingCustomer = optionalCustomer.get();
            if(existingCustomer.getEmail() != null && existingCustomer.getEmail().equals(customerUpdateDTO.getEmail()) &&
                    customerRepository.existsByEmailAndId(customerUpdateDTO.getEmail(),id)){
                throw new EntityNotFoundException("Customer with this email is already exists");
            }
            if(existingCustomer.getPhoneNumber() != null && existingCustomer.getPhoneNumber().equals(customerUpdateDTO.getEmail()) &&
                    customerRepository.existsByPhoneNumberAndId(customerUpdateDTO.getPhoneNumber(), id)) {
                throw new EntityNotFoundException("Customer with this phone number is already exists");
            }
            if(customerUpdateDTO.getEmail() != null){
                existingCustomer.setEmail(customerUpdateDTO.getEmail());
            }
            if(customerUpdateDTO.getPhoneNumber() != null){
                existingCustomer.setPhoneNumber(customerUpdateDTO.getPhoneNumber());
            }
            Customer updatedCustomer = customerRepository.save(existingCustomer);
//        later
            return new ApiResponse<>(toCustomerResponse(updatedCustomer), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "customers", key = "#id")
    public ApiResponse<Object> getCustomerById(Long id){
        try {
            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if (optionalCustomer.isEmpty()) {
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Customer customer = optionalCustomer.get();
            return new ApiResponse<>(toCustomerResponse(customer), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "customers")
    public ApiResponse<Object> getAll(Pageable pageable){
        try {
            Page<Customer> customerPage = customerRepository.findAll(pageable);
            return new ApiResponse<>(customerPage.map(this::toCustomerResponse), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }



    //helper
    private CustomerResponseDTO toCustomerResponse(Customer customer){
        return CustomerResponseDTO.builder().id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail()).phoneNumber(customer.getPhoneNumber()).build();
    }
    private CustomerSummaryDTO toCustomerSummaryDTO(Customer customer){
        return CustomerSummaryDTO.builder().firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail()).phoneNumber(customer.getPhoneNumber()).build();
    }
}
