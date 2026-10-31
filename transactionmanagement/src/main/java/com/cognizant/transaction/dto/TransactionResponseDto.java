package com.cognizant.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cognizant.transaction.entity.TransactionStatus;
import com.cognizant.transaction.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {

	private String transactionReference;
    private Long accountId;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDate transactionDate;
}

