package com.cognizant.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Customer registration and update request")
public class CustomersRequestDto {
	
	@NotBlank(message = "First name is required")
	@Size(max = 100, message = "First name cannot exceed 100 characters")
	@Schema(description = "Customer's first name", example = "Jordan", maxLength = 100)
	private String firstName;
	
	@NotBlank(message = "Last name is required")
	@Size(max = 100, message = "Last name cannot exceed 100 characters")
	@Schema(description = "Customer's last name", example = "Lee", maxLength = 100)
	private String lastName;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	@Size(max = 100, message = "Email cannot exceed 100 characters")
	@Schema(description = "Customer's email address (must be unique)", example = "jordan.lee@bank.com", format = "email", maxLength = 100)
	private String email;
	
	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid (e.g., +31 690000000)")
	@Size(max = 20, message = "Phone number cannot exceed 20 characters")
	@Schema(description = "Customer's phone number", example = "+31 690000000", pattern = "^\\+?[1-9]\\d{1,14}$", maxLength = 20)
	private String phoneNo;
	
	@NotBlank(message = "Address is required")
	@Size(max = 250, message = "Address cannot exceed 250 characters")
	@Schema(description = "Customer's address", example = "10 Bank Avenue, Rotterdam", maxLength = 250)
	private String address;
}
