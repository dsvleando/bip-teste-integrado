package com.example.backend.api.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.api.dto.BeneficioSearchDTO;
import com.example.backend.api.dto.PageDTO;
import com.example.backend.api.dto.TransferenciaDTO;
import com.example.backend.service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios. Permite criar, listar, atualizar, remover e transferir valores entre benefícios.")
@RestController
@RequestMapping("/api/v1/beneficios")
@RequiredArgsConstructor
public class BeneficioController {

	private final BeneficioService service;

	@Operation(summary = "Listar benefícios paginados", description = "Retorna uma página de benefícios com informações de paginação. Use os parâmetros 'page' (padrão: 0) e 'size' (padrão: 10) para controlar a paginação.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Página de benefícios retornada com sucesso", content = @Content(schema = @Schema(implementation = PageDTO.class))) })
	@GetMapping
	public ResponseEntity<PageDTO<BeneficioDTO>> listarTodosPaginados(
			@Parameter(description = "Parâmetros de paginação (page, size)", example = "page=0&size=10") @PageableDefault(size = 10, page = 0) Pageable pageable) {
		return ResponseEntity.ok(service.listarTodos(pageable));
	}

	@Operation(summary = "Buscar benefícios ativos por nome", description = "Busca benefícios ativos cujo nome contenha o termo fornecido. Retorna apenas informações básicas (sem saldo). Útil para autocomplete e seleção.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de benefícios encontrados", content = @Content(schema = @Schema(implementation = BeneficioSearchDTO.class))) })
	@GetMapping("/search")
	public ResponseEntity<List<BeneficioSearchDTO>> buscarAtivosPorNome(
			@Parameter(description = "Termo de busca (nome)", required = true, example = "Benefício") @org.springframework.web.bind.annotation.RequestParam String nome) {
		return ResponseEntity.ok(service.buscarAtivosPorNome(nome));
	}

	@Operation(summary = "Buscar benefício por ID", description = "Retorna um benefício específico baseado no ID fornecido.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Benefício encontrado", content = @Content(schema = @Schema(implementation = BeneficioDTO.class))),
			@ApiResponse(responseCode = "404", description = "Benefício não encontrado") })
	@GetMapping("/{id}")
	public ResponseEntity<BeneficioDTO> buscarPorId(
			@Parameter(description = "ID do benefício a ser buscado", required = true, example = "1") @PathVariable Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@Operation(summary = "Criar novo benefício", description = "Cria um novo benefício no sistema. O ID será gerado automaticamente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Benefício criado com sucesso", content = @Content(schema = @Schema(implementation = BeneficioDTO.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos") })
	@PostMapping
	public ResponseEntity<BeneficioDTO> criar(
			@Parameter(description = "Dados do benefício a ser criado", required = true) @Valid @RequestBody BeneficioDTO dto) {
		BeneficioDTO criado = service.criar(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@Operation(summary = "Atualizar benefício existente", description = "Atualiza os dados de um benefício existente baseado no ID fornecido.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso", content = @Content(schema = @Schema(implementation = BeneficioDTO.class))),
			@ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
			@ApiResponse(responseCode = "409", description = "Conflito de versão (entidade foi modificada por outro processo)") })
	@PutMapping("/{id}")
	public ResponseEntity<BeneficioDTO> atualizar(
			@Parameter(description = "ID do benefício a ser atualizado", required = true, example = "1") @PathVariable Long id,
			@Parameter(description = "Dados atualizados do benefício", required = true) @Valid @RequestBody BeneficioDTO dto) {
		BeneficioDTO atualizado = service.atualizar(id, dto);
		return ResponseEntity.ok(atualizado);
	}

	@Operation(summary = "Remover benefício", description = "Remove um benefício do sistema baseado no ID fornecido.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Benefício removido com sucesso"),
			@ApiResponse(responseCode = "404", description = "Benefício não encontrado") })
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(
			@Parameter(description = "ID do benefício a ser removido", required = true, example = "1") @PathVariable Long id) {
		service.remover(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Transferir valor entre benefícios", description = "Transfere um valor do benefício de origem para o benefício de destino. "
			+ "Valida se o benefício origem tem saldo suficiente e se ambos estão ativos. "
			+ "Usa optimistic locking para garantir consistência em ambientes concorrentes.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Erro na transferência: saldo insuficiente, benefício inativo, IDs inválidos ou valor inválido"),
			@ApiResponse(responseCode = "404", description = "Benefício origem ou destino não encontrado"),
			@ApiResponse(responseCode = "409", description = "Conflito de versão (entidade foi modificada por outro processo)") })
	@PostMapping("/transfer")
	public ResponseEntity<Void> transferir(
			@Parameter(description = "Dados da transferência (fromId, toId, amount)", required = true) @Valid @RequestBody TransferenciaDTO dto) {
		service.transferir(dto);
		return ResponseEntity.ok().build();
	}
}
