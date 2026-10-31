package com.cognizant.accounts.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cognizant.accounts.dto.CustomersResponseDto;



@FeignClient("customers")
public interface CustomerFeignClient {

	@GetMapping(path = "/api/{customerId}")
	ResponseEntity<CustomersResponseDto> getCustomerById(@PathVariable String customerId);
}
