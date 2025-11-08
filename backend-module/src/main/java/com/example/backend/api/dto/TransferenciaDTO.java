package com.example.backend.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Dados para transferência de valor entre dois benefícios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaDTO {

	@Schema(description = "ID do benefício de origem (de onde o valor será debitado)", example = "1", required = true)
	@NotNull(message = "ID de origem é obrigatório")
	private Long fromId;

	@Schema(description = "ID do benefício de destino (para onde o valor será creditado)", example = "2", required = true)
	@NotNull(message = "ID de destino é obrigatório")
	private Long toId;

	@Schema(description = "Valor a ser transferido", example = "100.00", required = true)
	@NotNull(message = "Valor é obrigatório")
	@DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
	@Digits(integer = 13, fraction = 2, message = "Valor inválido")
	private BigDecimal amount;
}
