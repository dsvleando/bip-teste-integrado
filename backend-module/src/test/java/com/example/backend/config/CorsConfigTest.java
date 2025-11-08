package com.example.backend.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class CorsConfigTest {

	@InjectMocks
	private CorsConfig config;

	@Test
	void testAddCorsMappings() {
		CorsRegistry registry = new CorsRegistry();
		config.addCorsMappings(registry);
		assertNotNull(registry);
	}

	@Test
	void testCorsFilter() {
		CorsFilter filter = config.corsFilter();
		assertNotNull(filter);
	}

	@Test
	void testCorsFilter_OptionsRequest() throws Exception {
		CorsFilter filter = config.corsFilter();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setMethod("OPTIONS");
		request.addHeader("Origin", "http://localhost:4200");
		request.addHeader("Access-Control-Request-Method", "POST");

		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		assertNotNull(response.getHeader("Access-Control-Allow-Origin"));
	}
}
