package com.cognizant.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages= "com.cognizant.transaction.service.client")
public class TransactionmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionmanagementApplication.class, args);
	}

}
