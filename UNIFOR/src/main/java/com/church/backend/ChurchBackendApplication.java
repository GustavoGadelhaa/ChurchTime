package com.church.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ChurchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChurchBackendApplication.class, args);
	}
}
