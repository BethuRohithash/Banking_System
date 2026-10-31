package com.cognizant.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.accounts.entity.Accounts;

public interface AccountRepository extends JpaRepository<Accounts, Long>{
 
    boolean existsByCustomerId(String customerId);

    boolean existsByAccountNumber(String accountNumber);
    
    Optional<Accounts> findByAccountId(Long accountId);
}
