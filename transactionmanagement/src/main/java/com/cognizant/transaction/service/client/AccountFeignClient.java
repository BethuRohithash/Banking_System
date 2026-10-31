package com.cognizant.transaction.service.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.cognizant.transaction.dto.AccountResponseDto;


@FeignClient("accounts")
public interface AccountFeignClient {


	@GetMapping("api/{accountId}")
	AccountResponseDto getAccountById(@PathVariable("accountId") Long accountId);

    @PutMapping("api/{accountId}/{balance}")
    ResponseEntity<?> updateAccountBalance(@PathVariable("accountId") Long accountId, @PathVariable("balance") BigDecimal newBalance);

}
