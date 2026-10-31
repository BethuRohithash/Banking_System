package com.cognizant.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cognizant.transaction.entity.AccountStatus;
import com.cognizant.transaction.entity.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private Long accountId;
    private String accountNumber;
    private String customerId;
    private AccountType accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private AccountStatus isActive;
}	
