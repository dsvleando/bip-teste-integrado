package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.example.backend", "com.example.beneficio.infrastructure" })
@EntityScan(basePackages = "com.example.beneficio.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.example.beneficio.infrastructure")
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
