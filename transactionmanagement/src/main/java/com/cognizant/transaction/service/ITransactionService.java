package com.cognizant.transaction.service;

import java.time.LocalDate;
import java.util.List;

import com.cognizant.transaction.dto.TransactionRequestDto;
import com.cognizant.transaction.dto.TransactionResponseDto;

public interface ITransactionService {

	TransactionResponseDto createTransaction(TransactionRequestDto requestDto);
	
	TransactionResponseDto getTransactionById(Long id);
	
	TransactionResponseDto getTransactionByReference(String reference);
	
	List<TransactionResponseDto> getTransactionsByAccountId(Long accountId);
	
    TransactionResponseDto updateTransaction(String transactionReferenceId, TransactionRequestDto updatedDto);
    
    boolean cancelTransaction(String transactionReferenceId);
    
    List<TransactionResponseDto> getTransactionByAccountAndRange(Long accountId, LocalDate start, LocalDate end);

}
