package com.example.ejb;

import java.math.BigDecimal;

import com.example.beneficio.infrastructure.persistence.entity.BeneficioEntity;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class BeneficioEjbService {

	@PersistenceContext
	private EntityManager em;

	public void transfer(Long fromId, Long toId, BigDecimal amount) {
		BeneficioEntity from = em.find(BeneficioEntity.class, fromId);
		BeneficioEntity to = em.find(BeneficioEntity.class, toId);

		// BUG: sem validações, sem locking, pode gerar saldo negativo e lost update
		from.setValor(from.getValor().subtract(amount));
		to.setValor(to.getValor().add(amount));

		em.merge(from);
		em.merge(to);
	}
}
