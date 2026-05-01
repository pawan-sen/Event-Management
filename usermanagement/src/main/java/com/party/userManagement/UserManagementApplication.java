package com.party.userManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.party.userManagement", "com.party.authManagement" })
@EnableJpaRepositories(basePackages = { "com.party.userManagement", "com.party.authManagement" })
@EntityScan(basePackages = { "com.party.userManagement", "com.party.authManagement" })
public class UserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}

}
