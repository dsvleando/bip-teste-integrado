package com.example.backend.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Representa um benefício para busca/seleção (sem informações de saldo)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioSearchDTO {

	@Schema(description = "ID único do benefício", example = "1")
	private Long id;

	@Schema(description = "Nome do benefício", example = "Benefício A")
	private String nome;

	@Schema(description = "Descrição do benefício", example = "Descrição detalhada do benefício")
	private String descricao;

}

