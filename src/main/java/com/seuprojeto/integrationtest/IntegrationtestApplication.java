package com.seuprojeto.integrationtest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication
public class IntegrationtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationtestApplication.class, args);
	}

}
