package com.cognizant.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cognizant.customer.dto.CustomersRequestDto;
import com.cognizant.customer.dto.CustomersResponseDto;
import com.cognizant.customer.entity.Customers;
import com.cognizant.customer.service.ImpI.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private CustomersRequestDto requestDto;
    private CustomersResponseDto responseDto;
    private Customers testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        // Set up test data
        requestDto = new CustomersRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john.doe@test.com");
        requestDto.setAddress("123 Test St");
        requestDto.setPhoneNo("1234567890");

        responseDto = new CustomersResponseDto();
        responseDto.setCustomerId("C12345");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setEmail("john.doe@test.com");
        responseDto.setAddress("123 Test St");
        responseDto.setPhoneNo("1234567890");

        testCustomer = new Customers();
        testCustomer.setCustomerId("C12345");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@test.com");
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        when(customerService.createCustomer(any(CustomersRequestDto.class))).thenReturn(responseDto);
        mockMvc.perform(post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@test.com"));
        verify(customerService).createCustomer(any(CustomersRequestDto.class));
    }

    @Test
    void testGetAllCustomers_Success() throws Exception {
        List<CustomersResponseDto> customerList = Arrays.asList(responseDto);
        when(customerService.getAllCustomers()).thenReturn(customerList);
        mockMvc.perform(get("/api/fetch/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].email").value("john.doe@test.com"));
        verify(customerService).getAllCustomers();
    }

    @Test
    void testGetCustomerById_Success() throws Exception {
        when(customerService.getCustomerById("C12345")).thenReturn(responseDto);
        mockMvc.perform(get("/api/fetch/C12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("C12345"))
                .andExpect(jsonPath("$.firstName").value("John"));
        verify(customerService).getCustomerById("C12345");
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        when(customerService.updateCustomer(eq("C12345"), any(CustomersRequestDto.class))).thenReturn(responseDto);
        mockMvc.perform(put("/api/update/C12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("C12345"));
        verify(customerService).updateCustomer(eq("C12345"), any(CustomersRequestDto.class));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        when(customerService.deleteCustomer("C12345")).thenReturn(true);
        mockMvc.perform(delete("/api/delete/C12345"))
                .andExpect(status().isOk());
        verify(customerService).deleteCustomer("C12345");
    }
}
