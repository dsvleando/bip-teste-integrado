package com.example.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("API de Benefícios").version("1.0.0")
						.description("API REST para gerenciamento de benefícios. "
								+ "Permite criar, listar, atualizar e remover benefícios, "
								+ "além de realizar transferências entre benefícios.")
						.contact(new Contact().name("Equipe de Desenvolvimento").email("dev@example.com"))
						.license(new License().name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.servers(List.of(new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento"),
						new Server().url("https://api.example.com").description("Servidor de Produção")));
	}
}
