package com.example.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@ExtendWith(MockitoExtension.class)
class OpenAPIConfigTest {

	@InjectMocks
	private OpenAPIConfig config;

	@Test
	void testCustomOpenAPI() {
		OpenAPI openAPI = config.customOpenAPI();

		assertNotNull(openAPI);
		assertNotNull(openAPI.getInfo());
		assertEquals("API de Benefícios", openAPI.getInfo().getTitle());
		assertEquals("1.0.0", openAPI.getInfo().getVersion());
		assertNotNull(openAPI.getServers());
		assertEquals(2, openAPI.getServers().size());

		Server devServer = openAPI.getServers().get(0);
		assertEquals("http://localhost:8080", devServer.getUrl());

		Server prodServer = openAPI.getServers().get(1);
		assertEquals("https://api.example.com", prodServer.getUrl());
	}
}
