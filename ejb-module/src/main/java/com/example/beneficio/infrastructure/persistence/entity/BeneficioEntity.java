package com.example.beneficio.infrastructure.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BENEFICIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "NOME", nullable = false, length = 100)
	private String nome;

	@Column(name = "DESCRICAO", length = 255)
	private String descricao;

	@Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
	private BigDecimal valor;

	@Column(name = "ATIVO")
	@Builder.Default
	private Boolean ativo = true;

	@Version
	@Column(name = "VERSION")
	private Long version;
}
