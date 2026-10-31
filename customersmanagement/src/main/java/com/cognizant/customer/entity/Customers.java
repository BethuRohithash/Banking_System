package com.cognizant.customer.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "customers",
	uniqueConstraints = {
			@UniqueConstraint(columnNames = {"email","phoneNo"})
	}
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String customerId;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNo;
	private String address;
	private LocalDateTime createdAt;
	@Enumerated(EnumType.STRING)
	private CustomerStatus status = CustomerStatus.PENDING;
}
