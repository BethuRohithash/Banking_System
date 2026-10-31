package com.cognizant.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cognizant.transaction.entity.TransactionType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Transaction creation and update request")
public class TransactionRequestDto {

    @NotNull(message = "Account ID is required")
    @Schema(description = "Account ID for the transaction", example = "2002", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long accountId;

    @NotNull(message = "Transaction type is required")
    @Schema(description = "Type of transaction", example = "DEPOSIT", allowableValues = {"DEPOSIT", "WITHDRAWAL"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Schema(description = "Transaction amount (must be positive)", example = "750.00", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
    
    @Schema(description = "Transaction date (optional, defaults to today for immediate transactions)", example = "2025-08-01", format = "date")
    private LocalDate transactionDate;
    
}

