package com.example.backend.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.beneficio.application.service.BeneficioService;
import com.example.beneficio.domain.port.input.BeneficioUseCase;
import com.example.beneficio.domain.port.output.BeneficioRepository;
import com.example.beneficio.infrastructure.adapter.input.BeneficioEjbAdapter;
import com.example.beneficio.infrastructure.adapter.output.JpaBeneficioRepository;
import com.example.beneficio.infrastructure.mapper.BeneficioMapper;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class BeneficioConfigTest {

	@Mock
	private EntityManager entityManager;

	private BeneficioConfig config;

	@BeforeEach
	void setUp() {
		config = new BeneficioConfig();
		ReflectionTestUtils.setField(config, "entityManager", entityManager);
	}

	@Test
	void testBeneficioMapper() {
		BeneficioMapper mapper = config.beneficioMapper();
		assertNotNull(mapper);
	}

	@Test
	void testBeneficioRepository() {
		BeneficioMapper mapper = config.beneficioMapper();
		BeneficioRepository repository = config.beneficioRepository(mapper);

		assertNotNull(repository);
		assertTrue(repository instanceof JpaBeneficioRepository);
	}

	@Test
	void testBeneficioUseCase() {
		BeneficioMapper mapper = config.beneficioMapper();
		BeneficioRepository repository = config.beneficioRepository(mapper);
		BeneficioUseCase useCase = config.beneficioUseCase(repository);

		assertNotNull(useCase);
		assertTrue(useCase instanceof BeneficioService);
	}

	@Test
	void testBeneficioEjbAdapter() {
		BeneficioMapper mapper = config.beneficioMapper();
		BeneficioRepository repository = config.beneficioRepository(mapper);
		BeneficioUseCase useCase = config.beneficioUseCase(repository);
		BeneficioEjbAdapter adapter = config.beneficioEjbAdapter(useCase);

		assertNotNull(adapter);
	}
}
