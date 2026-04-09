package com.fuzentrix.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class FuzentrixBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuzentrixBackendApplication.class, args);
	}

}
