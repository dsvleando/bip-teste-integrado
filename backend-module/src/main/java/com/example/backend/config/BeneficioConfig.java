package com.example.backend.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.beneficio.application.service.BeneficioService;
import com.example.beneficio.domain.port.input.BeneficioUseCase;
import com.example.beneficio.domain.port.output.BeneficioRepository;
import com.example.beneficio.infrastructure.adapter.input.BeneficioEjbAdapter;
import com.example.beneficio.infrastructure.adapter.output.JpaBeneficioRepository;
import com.example.beneficio.infrastructure.mapper.BeneficioMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class BeneficioConfig {

	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public BeneficioMapper beneficioMapper() {
		try {
			return Mappers.getMapper(BeneficioMapper.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Erro ao criar BeneficioMapper. Certifique-se de que o ejb-module foi compilado e instalado no repositório local do Maven (mvn clean install no ejb-module).",
					e);
		}
	}

	@Bean
	public BeneficioRepository beneficioRepository(BeneficioMapper mapper) {
		return new JpaBeneficioRepository(entityManager, mapper);
	}

	@Bean
	public BeneficioUseCase beneficioUseCase(BeneficioRepository repository) {
		return new BeneficioService(repository);
	}

	@Bean
	public BeneficioEjbAdapter beneficioEjbAdapter(BeneficioUseCase beneficioUseCase) {
		BeneficioEjbAdapter adapter = new BeneficioEjbAdapter();
		adapter.setBeneficioUseCase(beneficioUseCase);
		return adapter;
	}
}
