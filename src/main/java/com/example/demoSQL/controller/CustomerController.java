package com.example.demoSQL.controller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerSearchDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.security.entity.User;
import com.example.demoSQL.security.repository.UserRepository;
import com.example.demoSQL.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private final UserRepository userRepository;

    @PostMapping
    public ApiResponse<Object> createCustomer(@Valid @RequestBody CustomerCreateDTO customerCreateDTO){
        return customerService.createCustomer(customerCreateDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ApiResponse<Object> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO customerUpdateDTO){
        return customerService.updateCustomer(id,customerUpdateDTO);
    }

    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ApiResponse<Object> getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ApiResponse<Object> getAllCustomer
            (@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return customerService.getAll(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/search")
    public ApiResponse<Object> searchCustomers(@Valid @RequestBody CustomerSearchDTO customerSearchDTO, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return customerService.searchCustomers(customerSearchDTO, pageable);
    }
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getCustomerProfile(Authentication auth) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(auth.getName());

            if (userOpt.isEmpty()) {
                ApiResponse<Object> response = new ApiResponse<>(null, ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOpt.get();
            Customer customer = user.getCustomer();

            if (customer == null) {
                // No customer profile exists yet
                ApiResponse<Object> response = new ApiResponse<>(null, ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Prepare customer data for frontend
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("id", customer.getId());
            customerData.put("firstName", customer.getFirstName());
            customerData.put("lastName", customer.getLastName());
            customerData.put("email", customer.getEmail());
            customerData.put("phoneNumber", customer.getPhoneNumber());
            customerData.put("type", customer.getType().toString());
            customerData.put("createdDate", customer.getCreatedDate());

            ApiResponse<Object> response = new ApiResponse<>(customerData, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
