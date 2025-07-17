package com.example.demoSQL.contorller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.service.CustomerServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Validated
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl customerServiceImpl;


    @PostMapping
    public ApiResponse<Object> createCustomer(@Valid @RequestBody CustomerCreateDTO customerCreateDTO){
        return customerServiceImpl.createCustomer(customerCreateDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ApiResponse<Object> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO customerUpdateDTO){
        return customerServiceImpl.updateCustomer(id,customerUpdateDTO);
    }
    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ApiResponse<Object> getCustomerById(@PathVariable Long id){
        try{
            ApiResponse<Object> customerDTO = customerServiceImpl.getCustomerById(id);
            return new ApiResponse<>(customerDTO, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        }


    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ApiResponse<Object> getAllCustomer
            (@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return customerServiceImpl.getAll(pageable);
    }
}
