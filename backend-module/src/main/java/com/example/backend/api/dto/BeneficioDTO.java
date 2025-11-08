package com.example.backend.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Representa um benefício no sistema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioDTO {

	@Schema(description = "ID único do benefício", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Long id;

	@Schema(description = "Nome do benefício", example = "Benefício A", required = true, maxLength = 100)
	@NotBlank(message = "Nome é obrigatório")
	@Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
	private String nome;

	@Schema(description = "Descrição do benefício", example = "Descrição detalhada do benefício", maxLength = 255)
	@Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
	private String descricao;

	@Schema(description = "Valor (saldo) do benefício", example = "1000.00", required = true)
	@NotNull(message = "Valor é obrigatório")
	@DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
	@Digits(integer = 13, fraction = 2, message = "Valor inválido")
	private BigDecimal valor;

	@Schema(description = "Indica se o benefício está ativo", example = "true", defaultValue = "true")
	private Boolean ativo;

	@Schema(description = "Versão para controle de concorrência (optimistic locking)", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
	private Long version;
}
