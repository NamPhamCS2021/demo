package com.example.demoSQL.service;


import com.example.demoSQL.dto.customer.CustomerCreateDTO;
import com.example.demoSQL.dto.customer.CustomerResponseDTO;
import com.example.demoSQL.dto.customer.CustomerSummaryDTO;
import com.example.demoSQL.dto.customer.CustomerUpdateDTO;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponseDTO createCustomer(CustomerCreateDTO customerCreateDTO){
        if(customerRepository.existsByEmail(customerCreateDTO.getEmail()) ||
        customerRepository.existsByPhoneNumber(customerCreateDTO.getPhoneNumber())){
            throw new IllegalArgumentException("Customer with this email or phone is already exists");
        }

        Customer customer = new Customer();
        customer.setFirstName(customerCreateDTO.getFirstName());
        customer.setLastName(customerCreateDTO.getLastName());
        customer.setEmail(customerCreateDTO.getEmail());
        customer.setPhoneNumber(customerCreateDTO.getPhoneNumber());
        customerRepository.save(customer);

//        gotta do this later
        return toCustomerResponse(customer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO){
        Customer existingCustomer = customerRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Customer with id "+id+" not found"));

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
        return toCustomerResponse(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Customer with id "+id+" not found"));
        return toCustomerResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerSummaryDTO> getAll(Pageable pageable){
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.map(this::toCustomerSummaryDTO);
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
