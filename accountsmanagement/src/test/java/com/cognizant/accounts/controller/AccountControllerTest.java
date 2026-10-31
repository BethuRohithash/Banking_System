package com.cognizant.accounts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cognizant.accounts.dto.AccountRequestDto;
import com.cognizant.accounts.dto.AccountResponseDto;
import com.cognizant.accounts.service.ImpI.AccountsService;
import com.cognizant.accounts.entity.AccountType;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountsService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountRequestDto testRequest;
    private AccountResponseDto testResponse;

    @BeforeEach
    void setUp() {
        // Simple test data
        testRequest = AccountRequestDto.builder()
                .customerId("C2001")
                .accountType(AccountType.CHECKING)
                .initialDeposit(new BigDecimal("1000.00"))
                .build();

        testResponse = AccountResponseDto.builder()
                .accountId(2001L)
                .customerId("C2001")
                .balance(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        // Given
        when(accountService.createAccount(any())).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(2001L))
                .andExpect(jsonPath("$.customerId").value("C2001"));
    }

    @Test
    void testGetAccountById_Success() throws Exception {
        // Given
        when(accountService.getAccountById(2001L)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/{accountId}", 2001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2001L));
    }

    @Test
    void testUpdateBalance_Success() throws Exception {
        // Given
        BigDecimal newBalance = new BigDecimal("2000.00");
        when(accountService.updateBalance(2001L, newBalance)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/{accountId}/{balance}", 2001L, newBalance))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateAccount_InvalidData() throws Exception {
        // Given - Invalid request
        AccountRequestDto invalidRequest = AccountRequestDto.builder()
                .customerId(null) // Invalid
                .build();

        // When & Then
        mockMvc.perform(post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}