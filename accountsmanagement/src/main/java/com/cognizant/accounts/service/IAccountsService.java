package com.cognizant.accounts.service;

import java.math.BigDecimal;
import java.util.List;

import com.cognizant.accounts.dto.AccountRequestDto;
import com.cognizant.accounts.dto.AccountResponseDto;
import com.cognizant.accounts.entity.AccountStatus;

public interface IAccountsService {
	
	AccountResponseDto createAccount(AccountRequestDto requestDto);
	
	List<AccountResponseDto> getAllAccounts();
	
	AccountResponseDto getAccountById(Long id);
	
	AccountResponseDto updateBalance(Long accountId, BigDecimal newBalance);
	
	AccountResponseDto updateAccountInfo(Long accountId, AccountRequestDto requestDto);
	
	AccountResponseDto updateAccountStatus(Long accountId, AccountStatus status);
	
	AccountResponseDto deactivateAccount(Long accountId);

}
