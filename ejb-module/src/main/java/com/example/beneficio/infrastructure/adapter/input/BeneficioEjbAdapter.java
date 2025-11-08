package com.example.beneficio.infrastructure.adapter.input;

import java.math.BigDecimal;

import com.example.beneficio.domain.port.input.BeneficioUseCase;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeneficioEjbAdapter {

	private BeneficioUseCase beneficioUseCase;

	public void setBeneficioUseCase(BeneficioUseCase useCase) {
		this.beneficioUseCase = useCase;
	}

	public void transfer(Long fromId, Long toId, BigDecimal amount) {
		if (beneficioUseCase == null) {
			throw new IllegalStateException("BeneficioUseCase não foi configurado");
		}
		beneficioUseCase.transferir(fromId, toId, amount);
	}
}
