package com.cognizant.transaction.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.transaction.constants.TransactionConstants;
import com.cognizant.transaction.dto.ResponseDto;
import com.cognizant.transaction.dto.TransactionRequestDto;
import com.cognizant.transaction.dto.TransactionResponseDto;
import com.cognizant.transaction.service.ImpI.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "Transaction Management", description = "Transaction processing, transfers, and payment records operations")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping(path = "/create")
	@Operation(summary = "Create new transaction", description = "Initiate a new banking transaction (deposit or withdrawal)")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
	    @ApiResponse(responseCode = "400", description = "Invalid input data, insufficient funds, or validation failed"),
	    @ApiResponse(responseCode = "404", description = "Account not found")
	})
	public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequestDto request){
		
		TransactionResponseDto transaction =  transactionService.createTransaction(request);
		if(transaction!=null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(TransactionConstants.BAD_REQUEST_CODE, TransactionConstants.TRANSACTION_FAILED));
	}
	
	@GetMapping(path = "reference/{transactionReferenceId}")
	@Operation(summary = "Get transaction by reference", description = "Retrieve transaction details by reference ID")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Transaction found successfully"),
	    @ApiResponse(responseCode = "404", description = "Transaction not found")
	})
	public ResponseEntity<?> getTransactionByReference(
	    @Parameter(description = "Transaction reference ID", required = true) @PathVariable String transactionReferenceId){
		TransactionResponseDto transaction = transactionService.getTransactionByReference(transactionReferenceId);
		if(transaction!=null) {
			return ResponseEntity.status(HttpStatus.OK).body(transaction);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(TransactionConstants.NOT_FOUND_CODE, TransactionConstants.TRANSACTION_NOT_FOUND));
	}
	
	@GetMapping(path = "account/{accountId}")
	public ResponseEntity<?> getTransactionByAccountId(@PathVariable Long accountId){
		List<TransactionResponseDto> transaction = transactionService.getTransactionsByAccountId(accountId);
		if(transaction!=null) {
			return ResponseEntity.status(HttpStatus.OK).body(transaction);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(TransactionConstants.TRANSACTION_NOT_FOUND, TransactionConstants.TRANSACTION_NOT_FOUND));
	}
	
	@PutMapping(path = "update/{transactionReferenceId}")
	public ResponseEntity<?> updateTransactionDetails(@PathVariable String transactionReferenceId, @Valid @RequestBody TransactionRequestDto response){
		TransactionResponseDto updateTransaction = transactionService.updateTransaction(transactionReferenceId, response);
		if(updateTransaction!=null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(TransactionConstants.CREATED_CODE, TransactionConstants.TRANSACTION_UPDATED));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(TransactionConstants.BAD_REQUEST_CODE, TransactionConstants.TRANSACTION_UPDATION_FAILED));
	}
	
	
	@DeleteMapping(path = "cancel/{transactionReferenceId}")
	public ResponseEntity<?> cancelTransaction(@PathVariable String transactionReferenceId){
		boolean status = transactionService.cancelTransaction(transactionReferenceId);
		if(!status) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(TransactionConstants.SUCCESS_CODE, TransactionConstants.TRANSACTION_DELETION));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(TransactionConstants.SERVER_ERROR_CODE, TransactionConstants.TRANSACTION_DELETION_FAILED));
	}
	
	@GetMapping("/{accountId}/{startDate}/{endDate}")
	public ResponseEntity<?> getTransactionsByAccountAndDateRange(
	        @PathVariable("accountId") Long accountId,
	        @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
	    List<TransactionResponseDto> transactions = transactionService.getTransactionByAccountAndRange(accountId, startDate, endDate);
	    if(transactions.isEmpty()) {
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(TransactionConstants.NOT_FOUND_CODE, TransactionConstants.TRANSACTION_NOT_FOUND));
	    }
	    return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactions);
	}
	
}
