package com.cognizant.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.customer.entity.Customers;

public interface CustomerRepository extends JpaRepository<Customers, Integer>{

	Optional<Customers> findByEmail(String email);
	
	Optional<Customers> findByCustomerId(String customerId);
}
