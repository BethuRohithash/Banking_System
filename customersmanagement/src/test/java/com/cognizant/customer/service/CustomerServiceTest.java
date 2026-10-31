package com.cognizant.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cognizant.customer.dto.CustomersRequestDto;
import com.cognizant.customer.dto.CustomersResponseDto;
import com.cognizant.customer.entity.Customers;
import com.cognizant.customer.exception.CustomerAlreadyExistsException;
import com.cognizant.customer.exception.CustomerNotFoundException;
import com.cognizant.customer.repository.CustomerRepository;
import com.cognizant.customer.service.ImpI.CustomerService;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;
    private Customers testCustomer;
    private CustomersRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Set up test data
        testCustomer = new Customers();
        testCustomer.setCustomerId("12345");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@test.com");
        testCustomer.setAddress("123 Test St");
        testCustomer.setPhoneNo("1234567890");

        requestDto = new CustomersRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john.doe@test.com");
        requestDto.setAddress("123 Test St");
        requestDto.setPhoneNo("1234567890");
    }

    @Test
    void testCreateCustomer_Success() {
        // Given
        when(customerRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customers.class))).thenReturn(testCustomer);

        // When
        CustomersResponseDto result = customerService.createCustomer(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("john.doe@test.com", result.getEmail());
        verify(customerRepository).save(any(Customers.class));
    }

    @Test
    void testCreateCustomer_CustomerAlreadyExists() {
        // Given
        when(customerRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(testCustomer));

        // When & Then
        assertThrows(CustomerAlreadyExistsException.class, () -> {
            customerService.createCustomer(requestDto);
        });
        verify(customerRepository, never()).save(any(Customers.class));
    }

    @Test
    void testGetAllCustomers_Success() {
        // Given
        List<Customers> customerList = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customerList);

        // When
        List<CustomersResponseDto> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(customerRepository).findAll();
    }

    @Test
    void testGetCustomerById_Success() {
        // Given
        when(customerRepository.findByCustomerId("12345")).thenReturn(Optional.of(testCustomer));

        // When
        CustomersResponseDto result = customerService.getCustomerById("12345");

        // Then
        assertNotNull(result);
        assertEquals("12345", result.getCustomerId());
        assertEquals("John", result.getFirstName());
        verify(customerRepository).findByCustomerId("12345");
    }

    @Test
    void testGetCustomerById_CustomerNotFound() {
        // Given
        when(customerRepository.findByCustomerId("99999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById("99999");
        });
        verify(customerRepository).findByCustomerId("99999");
    }

    @Test
    void testDeleteCustomer_Success() {
        // Given
        when(customerRepository.findByCustomerId("12345")).thenReturn(Optional.of(testCustomer));

        // When
        boolean result = customerService.deleteCustomer("12345");

        // Then
        assertTrue(result);
        verify(customerRepository).findByCustomerId("12345");
        verify(customerRepository).save(testCustomer);
    }
}
