package com.cognizant.customer.service;

import java.util.List;

import com.cognizant.customer.dto.CustomersRequestDto;
import com.cognizant.customer.dto.CustomersResponseDto;

public interface ICustomerService {

	CustomersResponseDto createCustomer(CustomersRequestDto customerDto);
	
	CustomersResponseDto getCustomerById(String id);
	
	List<CustomersResponseDto> getAllCustomers();
	
	CustomersResponseDto updateCustomer(String customerId, CustomersRequestDto customerDto);
	
	boolean deleteCustomer(String customerId);
}
