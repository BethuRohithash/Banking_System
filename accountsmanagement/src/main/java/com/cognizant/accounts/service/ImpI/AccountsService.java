package com.cognizant.accounts.service.ImpI;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
 
import com.cognizant.accounts.dto.AccountRequestDto;
import com.cognizant.accounts.dto.AccountResponseDto;
import com.cognizant.accounts.dto.CustomersResponseDto;
import com.cognizant.accounts.entity.AccountStatus;
import com.cognizant.accounts.entity.AccountType;
import com.cognizant.accounts.entity.Accounts;
import com.cognizant.accounts.entity.CustomerStatus;
import com.cognizant.accounts.exception.AccessDeniedException;
import com.cognizant.accounts.exception.ResourceNotFoundException;
import com.cognizant.accounts.repository.AccountRepository;
import com.cognizant.accounts.service.IAccountsService;
import com.cognizant.accounts.service.client.CustomerFeignClient;
 
@Service
public class AccountsService implements IAccountsService{
	private final AccountRepository accountRepo;
	private final CustomerFeignClient customerClient;
 
	public AccountsService(AccountRepository accountRepo, CustomerFeignClient customerClient) {
		this.accountRepo = accountRepo;
		this.customerClient = customerClient;
	}
 	@Override
	@Transactional
	@Operation(summary = "Create new account", description = "Create a new bank account for an existing customer")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Account created successfully"),
		@ApiResponse(responseCode = "400", description = "Customer already has an account"),
		@ApiResponse(responseCode = "404", description = "Customer not found"),
		@ApiResponse(responseCode = "403", description = "Customer is inactive")
	})
	public AccountResponseDto createAccount(AccountRequestDto requestDto) {
		var response = customerClient.getCustomerById(requestDto.getCustomerId());
		if(response.getBody()==null) {
			throw new ResourceNotFoundException("Customer Not Found With Id:"+requestDto.getCustomerId());
		}
		if(response.getBody().getStatus()==CustomerStatus.INACTIVE) {
			throw new AccessDeniedException("Customer is InActive You can't have access to create account!!!!");
		}
		if (accountRepo.existsByCustomerId(requestDto.getCustomerId())) {
            throw new IllegalArgumentException("User already has an account.");
        }
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepo.existsByAccountNumber(accountNumber));
        
        Accounts account = Accounts.builder()
        	.accountId(generateAccountId())
            .accountNumber(accountNumber)
            .customerId(requestDto.getCustomerId())
            .accountType(requestDto.getAccountType())
            .balance(requestDto.getInitialDeposit() != null ? requestDto.getInitialDeposit() : BigDecimal.ZERO)
            .createdAt(LocalDateTime.now())
            .isActive(AccountStatus.ACTIVE)
            .build();

        account = accountRepo.save(account);
        return mapToResponseDto(account);
	}
 
	@Override
	@Operation(summary = "Get all accounts", description = "Retrieve all bank accounts in the system")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts")
	public List<AccountResponseDto> getAllAccounts() {
		return accountRepo.findAll()
	            .stream()
	            .map(this::mapToResponseDto)
	            .collect(Collectors.toList());
	}
 
	@Override
	@Operation(summary = "Get account by account ID", description = "Retrieve account information for a specific account")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Account found successfully"),
		@ApiResponse(responseCode = "404", description = "Account not found"),
		@ApiResponse(responseCode = "403", description = "Customer is inactive")
	})
	public AccountResponseDto getAccountById(Long accountId) {
		// First find the account by accountId
		Accounts account = accountRepo.findByAccountId(accountId)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found with account id: " + accountId));
		
		// Then verify the customer is active using the account's customerId
		CustomersResponseDto customer = customerClient.getCustomerById(account.getCustomerId()).getBody();
		if(customer != null && customer.getStatus() != CustomerStatus.ACTIVE) {
			throw new AccessDeniedException("Customer is InActive Pls Contact the Dev Team!!!!");
		}
		
	    return mapToResponseDto(account);
	}
 
 
	@Override
	@Operation(summary = "Update account balance", description = "Update the balance of an existing account")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Balance updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid balance amount"),
		@ApiResponse(responseCode = "404", description = "Account not found")
	})	public AccountResponseDto updateBalance(Long accountId, BigDecimal newBalance) {
		
		if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
	        throw new IllegalArgumentException("Account balance cannot be negative");
	    }
		
		Accounts account = accountRepo.findByAccountId(accountId)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found with account id: " + accountId));

	    account.setBalance(newBalance);
	    account = accountRepo.save(account);
	    return mapToResponseDto(account);
	}
	@Override
	@Operation(summary = "Update account information", description = "Update account type, status and other details")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Account updated successfully"),
		@ApiResponse(responseCode = "404", description = "Account not found")
	})
	public AccountResponseDto updateAccountInfo(Long accountId, AccountRequestDto requestDto) {
		Accounts account = accountRepo.findByAccountId(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found with account id: " + accountId));
		
		// Update account type if provided
		if(requestDto.getAccountType() != null) {
			account.setAccountType(requestDto.getAccountType());
		}
		
		// Update account status if provided
		if(requestDto.getAccountStatus() != null) {
			account.setIsActive(requestDto.getAccountStatus());
		}
		
		account = accountRepo.save(account);
		return mapToResponseDto(account);
	}

	@Override
	@Operation(summary = "Deactivate account", description = "Deactivate an existing account")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Account deactivated successfully"),
		@ApiResponse(responseCode = "404", description = "Account not found")
	})
	public AccountResponseDto deactivateAccount(Long accountId) {
		Accounts account = accountRepo.findByAccountId(accountId)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found with account id: " + accountId));
 
	    account.setIsActive(AccountStatus.INACTIVE);
 
	    account = accountRepo.save(account);
 
	    return mapToResponseDto(account);
	}
	
	@Override
	@Operation(summary = "Update account status", description = "Change account status to ACTIVE or INACTIVE")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Account status updated successfully"),
		@ApiResponse(responseCode = "404", description = "Account not found")
	})
	public AccountResponseDto updateAccountStatus(Long accountId, AccountStatus status) {
		Accounts account = accountRepo.findByAccountId(accountId)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found with account id: " + accountId));
 
	    account.setIsActive(status);
	    account = accountRepo.save(account);
 
	    return mapToResponseDto(account);
	}

	private String generateAccountNumber() {
        return "CTS" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
	private Long generateAccountId() {
		long base = 1000L; // Starting point for account IDs
    	long count = accountRepo.count() + 1; // Get current account numbers
    	return base + count;
	}
	private AccountResponseDto mapToResponseDto(Accounts account) {
        return AccountResponseDto.builder()
            .accountId(account.getAccountId())
            .accountNumber(account.getAccountNumber())
            .customerId(account.getCustomerId())
            .accountType(account.getAccountType())
            .balance(account.getBalance())
            .createdAt(account.getCreatedAt())
            .isActive(account.getIsActive())
            .build();
    }
 
}