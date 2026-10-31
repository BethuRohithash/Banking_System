package com.cognizant.customer.service.ImpI;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.customer.dto.CustomersRequestDto;
import com.cognizant.customer.dto.CustomersResponseDto;
import com.cognizant.customer.entity.CustomerStatus;
import com.cognizant.customer.entity.Customers;
import com.cognizant.customer.exception.CustomerAlreadyExistsException;
import com.cognizant.customer.exception.CustomerNotFoundException;
import com.cognizant.customer.repository.CustomerRepository;
import com.cognizant.customer.service.ICustomerService;

@Service
public class CustomerService implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public CustomersResponseDto createCustomer(CustomersRequestDto customerDto) {
        Optional<Customers> optCustomer = customerRepo.findByEmail(customerDto.getEmail());
        if (optCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already exists with given credentials.");
        }

        Customers customer = new Customers();
        customer.setCustomerId(generateCustomerId());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        customer.setAddress(customerDto.getAddress());
        customer.setPhoneNo(customerDto.getPhoneNo());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setStatus(CustomerStatus.ACTIVE);

        customerRepo.save(customer);
        return mapToCustomersResponseDto(customer);
    }

    
    @Override
    public List<CustomersResponseDto> getAllCustomers() {
        List<Customers> customers = customerRepo.findAll();
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("No customers found.");
        }
        return customers.stream()
                .map(this::mapToCustomersResponseDto)
                .toList();
    }

    @Override
    public CustomersResponseDto updateCustomer(String customerId, CustomersRequestDto customerDto) {
        Customers existingCustomer = customerRepo.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with the given input data ID : '" + customerId + "'"));

        existingCustomer.setFirstName(customerDto.getFirstName());
        existingCustomer.setLastName(customerDto.getLastName());
        existingCustomer.setEmail(customerDto.getEmail());
        existingCustomer.setAddress(customerDto.getAddress());
        existingCustomer.setPhoneNo(customerDto.getPhoneNo());

        customerRepo.save(existingCustomer);
        return mapToCustomersResponseDto(existingCustomer);
    }

    @Override
    public boolean deleteCustomer(String customerId) {
        Customers customer = customerRepo.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with the given input data ID : '" + customerId + "'"));

        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepo.save(customer);
        return true;
    }

    private CustomersResponseDto mapToCustomersResponseDto(Customers customer) {
        return CustomersResponseDto.builder()
            .customerId(customer.getCustomerId())
            .firstName(customer.getFirstName())
            .lastName(customer.getLastName())
            .email(customer.getEmail())
            .address(customer.getAddress())
            .phoneNo(customer.getPhoneNo())
            .createdAt(customer.getCreatedAt())
            .status(customer.getStatus())
            .build();
    }
    
    private String generateCustomerId() {
    	long base = 2000L; // Starting point for customer IDs
    	long count = customerRepo.count() + 1; // Get current customer count
    	return "C" + (base + count);
    }


    @Override
    public CustomersResponseDto getCustomerById(String id) {
        Customers customer = customerRepo.findByCustomerId(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with the given input data ID : '" + id + "'"));
        
        return mapToCustomersResponseDto(customer);
    }

}
