package com.example.demoSQL.contorller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerResponseDTO;
import com.example.demoSQL.dto.customer.CustomerSummaryDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.enums.ErrorMessage;
import com.example.demoSQL.service.CustomerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Validated
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl customerServiceImpl;


    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createCustomer(@Valid @RequestBody CustomerCreateDTO customerCreateDTO){
        ApiResponse<Object> createdDTO = customerServiceImpl.createCustomer(customerCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO customerUpdateDTO){
        ApiResponse<Object> updatedCustomer = customerServiceImpl.updateCustomer(id,customerUpdateDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getCustomerById(@PathVariable Long id){
        try{
            ApiResponse<Object> customerDTO = customerServiceImpl.getCustomerById(id);
            return ResponseEntity.ok(
                new ApiResponse<>(customerDTO, ErrorMessage.SUCCESS.getCode(), ErrorMessage.SUCCESS.getMessage())
            );
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), ErrorMessage.NOT_FOUND.getCode(), ErrorMessage.NOT_FOUND.getMessage()));
        }


    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllCustomer
            (@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        ApiResponse<Object> customerPage = customerServiceImpl.getAll(pageable);
        return ResponseEntity.ok(customerPage);
    }
}
