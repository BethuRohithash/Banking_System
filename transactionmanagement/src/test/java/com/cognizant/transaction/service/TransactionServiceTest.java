package com.cognizant.transaction.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cognizant.transaction.dto.AccountResponseDto;
import com.cognizant.transaction.dto.TransactionRequestDto;
import com.cognizant.transaction.dto.TransactionResponseDto;
import com.cognizant.transaction.entity.AccountStatus;
import com.cognizant.transaction.entity.Transaction;
import com.cognizant.transaction.entity.TransactionStatus;
import com.cognizant.transaction.entity.TransactionType;
import com.cognizant.transaction.exception.ResourceNotFoundException;
import com.cognizant.transaction.repository.TransactionRepository;
import com.cognizant.transaction.service.ImpI.TransactionService;
import com.cognizant.transaction.service.client.AccountFeignClient;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountFeignClient accountFeignClient;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequestDto requestDto;
    private Transaction transaction;
    private AccountResponseDto accountResponse;

    @BeforeEach
    void setUp() {
        requestDto = TransactionRequestDto.builder()
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .transactionDate(LocalDate.now())
                .build();

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionReference("TXN123456");
        transaction.setAccountId(1001L);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDate.now());

        accountResponse = AccountResponseDto.builder()
                .accountId(1001L)
                .customerId("C2001")
                .balance(new BigDecimal("1500.00"))
                .isActive(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void testCreateTransaction_Success() {
        // Given
        when(accountFeignClient.getAccountById(1001L)).thenReturn(accountResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionResponseDto result = transactionService.createTransaction(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("TXN123456", result.getTransactionReference());
        assertEquals(1001L, result.getAccountId());
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetTransactionByReference_Success() {
        // Given
        when(transactionRepository.findByTransactionReference("TXN123456"))
                .thenReturn(Optional.of(transaction));

        // When
        TransactionResponseDto result = transactionService.getTransactionByReference("TXN123456");

        // Then
        assertNotNull(result);
        assertEquals("TXN123456", result.getTransactionReference());
        assertEquals(1001L, result.getAccountId());
        verify(transactionRepository, times(1)).findByTransactionReference("TXN123456");
    }

    @Test
    void testGetTransactionByReference_NotFound() {
        // Given
        when(transactionRepository.findByTransactionReference("INVALID"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionByReference("INVALID");
        });
        verify(transactionRepository, times(1)).findByTransactionReference("INVALID");
    }

    @Test
    void testGetTransactionsByAccountId_Success() {
        // Given
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByAccountId(1001L)).thenReturn(transactions);

        // When
        List<TransactionResponseDto> result = transactionService.getTransactionsByAccountId(1001L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TXN123456", result.get(0).getTransactionReference());
        verify(transactionRepository, times(1)).findByAccountId(1001L);
    }

    @Test
    void testUpdateTransaction_Success() {
        // Given
        TransactionRequestDto updateRequest = TransactionRequestDto.builder()
                .accountId(1001L)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("600.00"))
                .build();

        Transaction scheduledTransaction = new Transaction();
        scheduledTransaction.setId(1L);
        scheduledTransaction.setTransactionReference("TXN123456");
        scheduledTransaction.setAccountId(1001L);
        scheduledTransaction.setType(TransactionType.DEPOSIT);
        scheduledTransaction.setAmount(new BigDecimal("500.00"));
        scheduledTransaction.setStatus(TransactionStatus.SCHEDULED); // Change to SCHEDULED
        scheduledTransaction.setTransactionDate(LocalDate.now());

        when(transactionRepository.findByTransactionReference("TXN123456"))
                .thenReturn(Optional.of(scheduledTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(scheduledTransaction);

        // When
        TransactionResponseDto result = transactionService.updateTransaction("TXN123456", updateRequest);

        // Then
        assertNotNull(result);
        verify(transactionRepository, times(1)).findByTransactionReference("TXN123456");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCancelTransaction_Success() {
        // Given
        Transaction scheduledTransaction = new Transaction();
        scheduledTransaction.setId(1L);
        scheduledTransaction.setTransactionReference("TXN123456");
        scheduledTransaction.setAccountId(1001L);
        scheduledTransaction.setType(TransactionType.DEPOSIT);
        scheduledTransaction.setAmount(new BigDecimal("500.00"));
        scheduledTransaction.setStatus(TransactionStatus.SCHEDULED); // Change to SCHEDULED
        scheduledTransaction.setTransactionDate(LocalDate.now());

        when(transactionRepository.findByTransactionReference("TXN123456"))
                .thenReturn(Optional.of(scheduledTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(scheduledTransaction);

        // When
        boolean result = transactionService.cancelTransaction("TXN123456");

        // Then
        assertFalse(result); // Method returns false for success
        verify(transactionRepository, times(1)).findByTransactionReference("TXN123456");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetTransactionByAccountAndRange_Success() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        List<Transaction> transactions = Arrays.asList(transaction);

        when(transactionRepository.findByAccountIdAndTransactionDateBetween(1001L, startDate, endDate))
                .thenReturn(transactions);

        // When
        List<TransactionResponseDto> result = transactionService.getTransactionByAccountAndRange(1001L, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TXN123456", result.get(0).getTransactionReference());
        verify(transactionRepository, times(1)).findByAccountIdAndTransactionDateBetween(1001L, startDate, endDate);
    }
}
