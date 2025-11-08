package com.example.beneficio.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficio {

	private Long id;
	private String nome;
	private String descricao;
	private BigDecimal valor;

	@Builder.Default
	private Boolean ativo = true;

	private Long version;

	public boolean isAtivo() {
		return Boolean.TRUE.equals(ativo);
	}

	public boolean temSaldoSuficiente(BigDecimal valor) {
		return this.valor != null && this.valor.compareTo(valor) >= 0;
	}

	public void decrementar(BigDecimal valor) {
		if (this.valor == null) {
			this.valor = BigDecimal.ZERO;
		}
		this.valor = this.valor.subtract(valor);
	}

	public void incrementar(BigDecimal valor) {
		if (this.valor == null) {
			this.valor = BigDecimal.ZERO;
		}
		this.valor = this.valor.add(valor);
	}
}
