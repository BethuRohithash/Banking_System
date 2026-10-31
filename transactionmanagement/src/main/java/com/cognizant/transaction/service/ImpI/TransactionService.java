package com.cognizant.transaction.service.ImpI;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.transaction.dto.AccountResponseDto;
import com.cognizant.transaction.dto.TransactionRequestDto;
import com.cognizant.transaction.dto.TransactionResponseDto;
import com.cognizant.transaction.entity.Transaction;
import com.cognizant.transaction.entity.TransactionStatus;
import com.cognizant.transaction.exception.InsufficientFundsException;
import com.cognizant.transaction.exception.ResourceNotFoundException;
import com.cognizant.transaction.repository.TransactionRepository;
import com.cognizant.transaction.service.ITransactionService;
import com.cognizant.transaction.service.client.AccountFeignClient;

@Service
public class TransactionService implements ITransactionService{

	private final TransactionRepository transactionRepo;
	private final AccountFeignClient accountFeign;
	
	public TransactionService(TransactionRepository transactionRepo, AccountFeignClient accountFeign) {
		this.transactionRepo = transactionRepo;
		this.accountFeign = accountFeign;
	}

	@Override
	public TransactionResponseDto createTransaction(TransactionRequestDto requestDto) {

	    // Validate account exists and is active
	    AccountResponseDto account = accountFeign.getAccountById(requestDto.getAccountId());

	    if (account == null) {
	        throw new ResourceNotFoundException("Account not found with id: " + requestDto.getAccountId());
	    }
	    
	    // Check if account is active
	    if (account.getIsActive() == null || !account.getIsActive().toString().equals("ACTIVE")) {
	        throw new IllegalArgumentException("Transaction cannot be processed. Account is not active.");
	    }

	    // Validate transaction date - compare dates only (not times)
	    // If transaction date is null, today, or in the past, process immediately
	    // If transaction date is in the future, schedule it
	    TransactionStatus status;
	    LocalDate today = LocalDate.now();
	    LocalDate transactionDate = requestDto.getTransactionDate();
	    
	    if (transactionDate == null || transactionDate.isBefore(today) || transactionDate.isEqual(today)) {
	        status = TransactionStatus.COMPLETED; // Process immediately
	    } else {
	        status = TransactionStatus.SCHEDULED; // Schedule for future
	    }

	    TransactionResponseDto responseDto;

	    switch (requestDto.getType()) {
	        case DEPOSIT:
	            responseDto = handleDeposit(requestDto, account, status);
	            break;
	        case WITHDRAWAL:
	            responseDto = handleWithdrawal(requestDto, account, status);
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid transaction type: " + requestDto.getType());
	    }

	    return responseDto;
	}

	
	private TransactionResponseDto handleDeposit(TransactionRequestDto requestDto, AccountResponseDto account, TransactionStatus status) {
	    if (status == TransactionStatus.COMPLETED) {
	        BigDecimal newBalance = account.getBalance().add(requestDto.getAmount());
	        updateAccountBalance(account.getAccountId(), newBalance);
	    }

	    Transaction transaction = buildTransaction(requestDto, status);
	    transaction = transactionRepo.save(transaction);

	    return mapToResponseDto(transaction);
	}

	private TransactionResponseDto handleWithdrawal(TransactionRequestDto requestDto, AccountResponseDto account, TransactionStatus status) {
	    if (status == TransactionStatus.COMPLETED) {
	        if (account.getBalance().compareTo(requestDto.getAmount()) < 0) {
	            throw new InsufficientFundsException("Insufficient funds in account: " + account.getAccountNumber());
	        }

	        BigDecimal newBalance = account.getBalance().subtract(requestDto.getAmount());
	        updateAccountBalance(account.getAccountId(), newBalance);
	    }

	    Transaction transaction = buildTransaction(requestDto, status);
	    transaction = transactionRepo.save(transaction);

	    return mapToResponseDto(transaction);
	}

	 

	 private void updateAccountBalance(Long accountId, BigDecimal newBalance) {
	        accountFeign.updateAccountBalance(accountId, newBalance);
	    }
	@Override
	public TransactionResponseDto getTransactionById(Long id) {
		Transaction transaction = transactionRepo.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
	        return mapToResponseDto(transaction);
	}

	@Override
	public TransactionResponseDto getTransactionByReference(String reference) {
		Transaction transaction = transactionRepo.findByTransactionReference(reference)
	            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + reference));
	    return mapToResponseDto(transaction);
	}

	
	@Override
	public List<TransactionResponseDto> getTransactionsByAccountId(Long accountId) {
		
		return transactionRepo.findByAccountId(accountId)
	            .stream()
	            .map(this::mapToResponseDto)
	            .collect(Collectors.toList());
	}
	
	private Transaction buildTransaction(TransactionRequestDto requestDto, TransactionStatus status) {
	    return Transaction.builder()
	        .transactionReference(generateTransactionReference())
	        .accountId(requestDto.getAccountId())
	        .amount(requestDto.getAmount())
	        .type(requestDto.getType())
	        .status(status)
	        .transactionDate(requestDto.getTransactionDate() != null ? requestDto.getTransactionDate() : LocalDate.now())
	        .build();
	}

	
	private String generateTransactionReference() {
        return "TRX" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private TransactionResponseDto mapToResponseDto(Transaction transaction) {
        return TransactionResponseDto.builder()
            .transactionReference(transaction.getTransactionReference())
            .accountId(transaction.getAccountId())
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .status(transaction.getStatus())
            .transactionDate(transaction.getTransactionDate())
            .build();
    }
    
    private List<TransactionResponseDto> mapToListResponseDto(List<Transaction> transactions) {
        return transactions.stream()
            .map(transaction -> TransactionResponseDto.builder()
                .transactionReference(transaction.getTransactionReference())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .transactionDate(transaction.getTransactionDate())
                .build())
            .collect(Collectors.toList());
    }

	@Override
	public TransactionResponseDto updateTransaction(String transactionReferenceId, TransactionRequestDto updatedDto) {

		Transaction transaction = transactionRepo.findByTransactionReference(transactionReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a completed transaction");
        }

        transaction.setAmount(updatedDto.getAmount());
        transaction.setType(updatedDto.getType());

        transaction = transactionRepo.save(transaction);
        return mapToResponseDto(transaction);

	}

	@Override
	public boolean cancelTransaction(String transactionReferenceId) {

		Transaction transaction = transactionRepo.findByTransactionReference(transactionReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled transactions can be cancelled");
        }

        transaction.setStatus(TransactionStatus.CANCELLED);
        Transaction transactionStatus = transactionRepo.save(transaction);
        if(transactionStatus!=null) {
        	return false;
        }
        return true;
	}

	@Override
	public List<TransactionResponseDto> getTransactionByAccountAndRange(Long accountId, LocalDate start,
			LocalDate end) {
		List<Transaction> transactions = transactionRepo.findByAccountIdAndTransactionDateBetween(accountId, start, end);
		if(transactions.isEmpty()) {
			throw new ResourceNotFoundException("No Transaction Details for this account Id:"+accountId);
		}
		return mapToListResponseDto(transactions);
	}

}
