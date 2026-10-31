package com.cognizant.transaction.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.transaction.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

    Optional<Transaction> findByTransactionReference(String reference);
    List<Transaction> findByAccountId(Long accountId);
    
    List<Transaction> findByAccountIdAndTransactionDateBetween(Long accountId,LocalDate startDate, LocalDate endDate);
}
