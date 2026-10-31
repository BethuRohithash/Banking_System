package com.cognizant.customer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.customer.constants.CustomerConstants;
import com.cognizant.customer.dto.CustomersRequestDto;
import com.cognizant.customer.dto.CustomersResponseDto;
import com.cognizant.customer.dto.ResponseDto;
import com.cognizant.customer.entity.Customers;
import com.cognizant.customer.service.ImpI.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "Customer Management", description = "Customer registration, profile management, and deletion operations")
public class CustomerController {

	private CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}
	
	@GetMapping(path = "/fetch/all")
	@Operation(summary = "Get all customers", description = "Retrieve all registered customers")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully retrieved customers"),
	    @ApiResponse(responseCode = "404", description = "No customers found")
	})
	public ResponseEntity<List<CustomersResponseDto>> fetchAllCustomers(){
		List<CustomersResponseDto> customers = customerService.getAllCustomers();
		return ResponseEntity.ok(customers);
	}
	
	@PostMapping("/create")
	@Operation(summary = "Register new customer", description = "Create a new customer profile with validation")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "201", description = "Customer created successfully"),
	    @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed"),
	    @ApiResponse(responseCode = "409", description = "Customer already exists with given email")
	})
	public ResponseEntity<CustomersResponseDto> addCustomer(@Valid @RequestBody CustomersRequestDto customerDto){
		CustomersResponseDto responseDto = customerService.createCustomer(customerDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
	
	@GetMapping(path = "/fetch/{customerId}")
	@Operation(summary = "Get customer by ID", description = "Retrieve customer profile by customer ID")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Customer found successfully"),
	    @ApiResponse(responseCode = "404", description = "Customer not found")
	})
	public ResponseEntity<CustomersResponseDto> getCustomerById(
	    @Parameter(description = "Customer ID", required = true) @PathVariable String customerId){
		CustomersResponseDto customerDto = customerService.getCustomerById(customerId);
		return ResponseEntity.ok(customerDto);
	}
	
	// Additional endpoint to support Account service Feign client call
	@GetMapping(path = "/{customerId}")
	@Operation(summary = "Get customer by ID (Feign client)", description = "Retrieve customer profile by customer ID for inter-service communication")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Customer found successfully"),
	    @ApiResponse(responseCode = "404", description = "Customer not found")
	})
	public ResponseEntity<CustomersResponseDto> getCustomerByIdForFeign(
	    @Parameter(description = "Customer ID", required = true) @PathVariable String customerId){
		CustomersResponseDto customerDto = customerService.getCustomerById(customerId);
		return ResponseEntity.ok(customerDto);
	}
	
	@PutMapping(path = "/update/{customerId}")
	@Operation(summary = "Update customer", description = "Update customer profile information")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
	    @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed"),
	    @ApiResponse(responseCode = "404", description = "Customer not found")
	})
	public ResponseEntity<CustomersResponseDto> updateCustomer(
	    @Parameter(description = "Customer ID", required = true) @PathVariable String customerId, 
	    @Valid @RequestBody CustomersRequestDto customerDto){
		CustomersResponseDto updateCustomer = customerService.updateCustomer(customerId, customerDto);
		return ResponseEntity.ok(updateCustomer);
	}
	
	@DeleteMapping(path = "/delete/{customerId}")
	@Operation(summary = "Delete customer", description = "Delete customer profile")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
	    @ApiResponse(responseCode = "404", description = "Customer not found")
	})
	public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId){
		boolean status = customerService.deleteCustomer(customerId);
		return ResponseEntity.ok().build();
	}
}
