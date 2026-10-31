package com.cognizant.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.cognizant.accounts.service.client")
public class AccountsmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsmanagementApplication.class, args);
	}

}
