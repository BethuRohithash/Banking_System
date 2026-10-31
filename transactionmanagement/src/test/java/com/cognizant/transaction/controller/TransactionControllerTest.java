package com.cognizant.transaction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cognizant.transaction.dto.TransactionRequestDto;
import com.cognizant.transaction.dto.TransactionResponseDto;
import com.cognizant.transaction.entity.TransactionStatus;
import com.cognizant.transaction.entity.TransactionType;
import com.cognizant.transaction.service.ImpI.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTransaction() throws Exception {
        // Given
        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .build();

        TransactionResponseDto responseDto = TransactionResponseDto.builder()
                .transactionReference("TXN123456")
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .status(TransactionStatus.COMPLETED)
                .transactionDate(LocalDate.now())
                .build();

        when(transactionService.createTransaction(any(TransactionRequestDto.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionReference").value("TXN123456"))
                .andExpect(jsonPath("$.accountId").value(1001))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    void testCreateTransactionFailure() throws Exception {
        // Given
        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .accountId(1001L)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("500.00"))
                .build();

        when(transactionService.createTransaction(any(TransactionRequestDto.class)))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTransactionByReference() throws Exception {
        // Given
        String transactionRef = "TXN123456";
        TransactionResponseDto responseDto = TransactionResponseDto.builder()
                .transactionReference(transactionRef)
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .status(TransactionStatus.COMPLETED)
                .transactionDate(LocalDate.now())
                .build();

        when(transactionService.getTransactionByReference(transactionRef))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/reference/{transactionReferenceId}", transactionRef))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionReference").value(transactionRef))
                .andExpect(jsonPath("$.accountId").value(1001));
    }

    @Test
    void testGetTransactionByReferenceNotFound() throws Exception {
        // Given
        String transactionRef = "INVALID";
        when(transactionService.getTransactionByReference(transactionRef))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/reference/{transactionReferenceId}", transactionRef))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTransactionByAccountId() throws Exception {
        // Given
        Long accountId = 1001L;
        List<TransactionResponseDto> transactions = Arrays.asList(
                TransactionResponseDto.builder()
                        .transactionReference("TXN123456")
                        .accountId(accountId)
                        .type(TransactionType.DEPOSIT)
                        .amount(new BigDecimal("500.00"))
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(LocalDate.now())
                        .build()
        );

        when(transactionService.getTransactionsByAccountId(accountId))
                .thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/account/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionReference").value("TXN123456"))
                .andExpect(jsonPath("$[0].accountId").value(1001));
    }

    @Test
    void testUpdateTransactionDetails() throws Exception {
        // Given
        String transactionRef = "TXN123456";
        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("600.00"))
                .build();

        TransactionResponseDto responseDto = TransactionResponseDto.builder()
                .transactionReference(transactionRef)
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("600.00"))
                .status(TransactionStatus.COMPLETED)
                .transactionDate(LocalDate.now())
                .build();

        when(transactionService.updateTransaction(anyString(), any(TransactionRequestDto.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/update/{transactionReferenceId}", transactionRef)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCancelTransaction() throws Exception {
        // Given
        String transactionRef = "TXN123456";
        when(transactionService.cancelTransaction(transactionRef))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/cancel/{transactionReferenceId}", transactionRef))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionsByAccountAndDateRange() throws Exception {
        // Given
        Long accountId = 1001L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        List<TransactionResponseDto> transactions = Arrays.asList(
                TransactionResponseDto.builder()
                        .transactionReference("TXN123456")
                        .accountId(accountId)
                        .type(TransactionType.DEPOSIT)
                        .amount(new BigDecimal("500.00"))
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(LocalDate.of(2023, 6, 1))
                        .build()
        );

        when(transactionService.getTransactionByAccountAndRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/{accountId}/{startDate}/{endDate}", accountId, startDate, endDate))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$[0].transactionReference").value("TXN123456"));
    }
}
