package com.cognizant.accounts.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.cognizant.accounts.dto.AccountRequestDto;
import com.cognizant.accounts.dto.AccountResponseDto;
import com.cognizant.accounts.dto.CustomersResponseDto;
import com.cognizant.accounts.entity.Accounts;
import com.cognizant.accounts.entity.AccountType;
import com.cognizant.accounts.entity.AccountStatus;
import com.cognizant.accounts.entity.CustomerStatus;
import com.cognizant.accounts.exception.ResourceNotFoundException;
import com.cognizant.accounts.repository.AccountRepository;
import com.cognizant.accounts.service.ImpI.AccountsService;
import com.cognizant.accounts.service.client.CustomerFeignClient;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private CustomerFeignClient customerClient;

    @InjectMocks
    private AccountsService accountsService;

    private AccountRequestDto testRequest;

    @BeforeEach
    void setUp() {        
        testRequest = AccountRequestDto.builder()
                .customerId("C2001")
                .accountType(AccountType.CHECKING)
                .initialDeposit(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void testCreateAccount_Success() {
        // Given
        CustomersResponseDto customerResponse = CustomersResponseDto.builder()
                .customerId("C2001")
                .firstName("John")
                .lastName("Doe")
                .status(CustomerStatus.ACTIVE)
                .build();
        
        ResponseEntity<CustomersResponseDto> responseEntity = ResponseEntity.ok(customerResponse);
        when(customerClient.getCustomerById("C2001")).thenReturn(responseEntity);
        when(accountRepo.existsByCustomerId("C2001")).thenReturn(false);
        when(accountRepo.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepo.count()).thenReturn(0L);
        
        Accounts savedAccount = new Accounts();
        savedAccount.setAccountId(1001L);
        savedAccount.setCustomerId("C2001");
        savedAccount.setAccountType(AccountType.CHECKING);
        savedAccount.setBalance(new BigDecimal("1000.00"));
        savedAccount.setIsActive(AccountStatus.ACTIVE);
        
        when(accountRepo.save(any(Accounts.class))).thenReturn(savedAccount);

        // When
        AccountResponseDto result = accountsService.createAccount(testRequest);

        // Then
        assertNotNull(result);
        assertEquals(1001L, result.getAccountId());
        assertEquals("C2001", result.getCustomerId());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        verify(accountRepo, times(1)).save(any(Accounts.class));
        verify(customerClient, times(1)).getCustomerById("C2001");
    }

    @Test
    void testGetAccountById_Success() {
        // Given
        Accounts account = new Accounts();
        account.setAccountId(2001L);
        account.setCustomerId("C2001");
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(new BigDecimal("1000.00"));
        
        CustomersResponseDto customerResponse = CustomersResponseDto.builder()
                .customerId("C2001")
                .firstName("John")
                .lastName("Doe")
                .status(CustomerStatus.ACTIVE)
                .build();
        
        ResponseEntity<CustomersResponseDto> responseEntity = ResponseEntity.ok(customerResponse);
        
        when(accountRepo.findByAccountId(2001L)).thenReturn(Optional.of(account));
        when(customerClient.getCustomerById("C2001")).thenReturn(responseEntity);

        // When
        AccountResponseDto result = accountsService.getAccountById(2001L);

        // Then
        assertNotNull(result);
        assertEquals(2001L, result.getAccountId());
        assertEquals("C2001", result.getCustomerId());
        verify(accountRepo, times(1)).findByAccountId(2001L);
        verify(customerClient, times(1)).getCustomerById("C2001");
    }

    @Test
    void testGetAccountById_NotFound() {
        // Given
        when(accountRepo.findByAccountId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            accountsService.getAccountById(999L);
        });
        verify(accountRepo, times(1)).findByAccountId(999L);
    }

    @Test
    void testUpdateBalance_Success() {
        // Given
        BigDecimal newBalance = new BigDecimal("2000.00");
        Accounts account = new Accounts();
        account.setAccountId(2001L);
        account.setBalance(new BigDecimal("1000.00"));
        
        when(accountRepo.findByAccountId(2001L)).thenReturn(Optional.of(account));
        when(accountRepo.save(any(Accounts.class))).thenReturn(account);

        // When
        AccountResponseDto result = accountsService.updateBalance(2001L, newBalance);

        // Then
        assertNotNull(result);
        assertEquals(newBalance, result.getBalance());
        verify(accountRepo, times(1)).findByAccountId(2001L);
        verify(accountRepo, times(1)).save(any(Accounts.class));
    }
}