package com.cognizant.customer.dto;

import java.time.LocalDateTime;

import com.cognizant.customer.entity.CustomerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomersResponseDto {

	private String customerId;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNo;
	private String address;
	private LocalDateTime createdAt;
	private CustomerStatus status;
}
