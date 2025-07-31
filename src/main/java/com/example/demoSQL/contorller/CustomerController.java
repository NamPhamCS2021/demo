package com.example.demoSQL.contorller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerSearchDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.enums.CustomerType;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.service.CustomerService;
import com.example.demoSQL.service.CustomerServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;



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
}
