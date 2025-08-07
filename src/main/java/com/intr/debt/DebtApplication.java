package com.intr.debt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DebtApplication {

	public static void main(String[] args) {
		SpringApplication.run(DebtApplication.class, args);
	}

}
