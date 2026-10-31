package com.cognizant.accounts.dto;

import java.math.BigDecimal;

import com.cognizant.accounts.entity.AccountStatus;
import com.cognizant.accounts.entity.AccountType;

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
@Schema(description = "Account creation and update request")
public class AccountRequestDto {
    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer ID who will own the account", example = "C2001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @NotNull(message = "Account type is required")
    @Schema(description = "Type of bank account", example = "SAVINGS", allowableValues = {"SAVINGS", "CHECKING", "CREDIT"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private AccountType accountType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Initial deposit cannot be negative")
    @Schema(description = "Initial deposit amount (must be non-negative)", example = "5000.00", minimum = "0")
    private BigDecimal initialDeposit;
    
    @Schema(description = "Account status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private AccountStatus accountStatus;
}