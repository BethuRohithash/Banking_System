package com.cognizant.accounts.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.accounts.constants.AccountsConstants;
import com.cognizant.accounts.dto.AccountRequestDto;
import com.cognizant.accounts.dto.AccountResponseDto;
import com.cognizant.accounts.dto.ResponseDto;
import com.cognizant.accounts.entity.AccountStatus;
import com.cognizant.accounts.service.ImpI.AccountsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "Account Management", description = "Account creation, updates, viewing, and deletion operations")
public class AccountController {

	@Autowired
    private  AccountsService accountService;

    @PostMapping(path = "/create")
    @Operation(summary = "Create new account", description = "Open a new bank account for an existing customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequestDto requestDto) {
    	AccountResponseDto newAccount = accountService.createAccount(requestDto);
    	if(newAccount!=null) {
        	return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    	}
    	return 	ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(AccountsConstants.SERVER_ERROR_CODE, AccountsConstants.ACCOUNT_NOT_CREATED));
    }
    
    @GetMapping(path = "/fetch/all")
    @Operation(summary = "Get all accounts", description = "Retrieve all bank accounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved accounts")
    })
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
    	return ResponseEntity.ok(accountService.getAllAccounts());
    }
    
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId) {
        AccountResponseDto account = accountService.getAccountById(accountId);
        if(account!=null) {
        	return ResponseEntity.status(HttpStatus.OK).body(account);
        }
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(AccountsConstants.NOT_FOUND_CODE, AccountsConstants.ACCOUNT_NOT_FOUND));
    }
    
    
    @PutMapping("update/{accountId}")
    @Operation(summary = "Update account information", description = "Update account type and status (ACTIVE/INACTIVE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResponseDto> updateAccountInfo(
    @PathVariable Long accountId,
    @RequestBody AccountRequestDto requestDto) {
    AccountResponseDto response = accountService.updateAccountInfo(accountId, requestDto);
    return ResponseEntity.ok(response);
}
    
    @PutMapping("{accountId}/{balance}")
    ResponseEntity<?> updateAccountBalance(@PathVariable("accountId") Long accountId, @PathVariable("balance") BigDecimal newBalance){
    	AccountResponseDto updateAccount = accountService.updateBalance(accountId, newBalance);
    	if(updateAccount!=null) {
    		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(AccountsConstants.SUCCESS_CODE, AccountsConstants.ACCOUNT_UPDATED));
    	}
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(AccountsConstants.NOT_FOUND_CODE, AccountsConstants.UPDATE_NOT_CREATED));
    }
    
    @PutMapping("/update-status/{accountId}")
    public ResponseEntity<AccountResponseDto> updateAccountStatus(
        @PathVariable Long accountId,
        @RequestParam AccountStatus status) {
        AccountResponseDto response = accountService.updateAccountStatus(accountId, status);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("close/{accountId}")
    public ResponseEntity<?> deactivateAccount(@PathVariable Long accountId) {
       AccountResponseDto status = accountService.deactivateAccount(accountId);
       if(status==null) {
    	   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(AccountsConstants.NOT_FOUND_CODE, AccountsConstants.ACCOUNT_NOT_FOUND));
       }
       return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(AccountsConstants.SUCCESS_CODE, AccountsConstants.ACCOUNT_DEACTIVATED));
    }

}
