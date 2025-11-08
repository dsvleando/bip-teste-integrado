package com.example.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BeneficioControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BeneficioService beneficioService;

	private BeneficioDTO beneficio1;
	private BeneficioDTO beneficio2;

	@BeforeEach
	void setUp() {
		beneficio1 = BeneficioDTO.builder().nome("Benefício Teste 1").descricao("Descrição 1")
				.valor(new BigDecimal("1000.00")).ativo(true).build();

		beneficio2 = BeneficioDTO.builder().nome("Benefício Teste 2").descricao("Descrição 2")
				.valor(new BigDecimal("500.00")).ativo(true).build();
	}

	@Test
	void testCriarBeneficio() throws Exception {
		String json = objectMapper.writeValueAsString(beneficio1);

		mockMvc.perform(post("/api/v1/beneficios").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.nome").value("Benefício Teste 1")).andExpect(jsonPath("$.valor").value(1000.00));
	}

	@Test
	void testListarTodosPaginados() throws Exception {
		beneficioService.criar(beneficio1);
		beneficioService.criar(beneficio2);

		mockMvc.perform(get("/api/v1/beneficios").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.size").value(10))
				.andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(1))
				.andExpect(jsonPath("$.first").value(true)).andExpect(jsonPath("$.last").value(true));
	}

	@Test
	void testBuscarPorId() throws Exception {
		BeneficioDTO criado = beneficioService.criar(beneficio1);

		mockMvc.perform(get("/api/v1/beneficios/{id}", criado.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(criado.getId()))
				.andExpect(jsonPath("$.nome").value("Benefício Teste 1"));
	}

	@Test
	void testBuscarPorId_NaoEncontrado() throws Exception {
		mockMvc.perform(get("/api/v1/beneficios/{id}", 999L)).andExpect(status().isNotFound());
	}

	@Test
	void testAtualizarBeneficio() throws Exception {
		BeneficioDTO criado = beneficioService.criar(beneficio1);
		criado.setNome("Nome Atualizado");
		criado.setValor(new BigDecimal("2000.00"));

		String json = objectMapper.writeValueAsString(criado);

		mockMvc.perform(
				put("/api/v1/beneficios/{id}", criado.getId()).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Nome Atualizado"))
				.andExpect(jsonPath("$.valor").value(2000.00));
	}

	@Test
	void testAtualizarBeneficio_NaoEncontrado() throws Exception {
		beneficio1.setId(999L);
		String json = objectMapper.writeValueAsString(beneficio1);

		mockMvc.perform(put("/api/v1/beneficios/{id}", 999L).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotFound());
	}

	@Test
	void testRemoverBeneficio() throws Exception {
		BeneficioDTO criado = beneficioService.criar(beneficio1);

		mockMvc.perform(delete("/api/v1/beneficios/{id}", criado.getId())).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/v1/beneficios/{id}", criado.getId())).andExpect(status().isNotFound());
	}

	@Test
	void testRemoverBeneficio_NaoEncontrado() throws Exception {
		mockMvc.perform(delete("/api/v1/beneficios/{id}", 999L)).andExpect(status().isNotFound());
	}

	@Test
	void testTransferir_Sucesso() throws Exception {
		BeneficioDTO origem = beneficioService.criar(beneficio1);
		BeneficioDTO destino = beneficioService.criar(beneficio2);

		String transferenciaJson = String.format("{\"fromId\": %d, \"toId\": %d, \"amount\": 100.00}", origem.getId(),
				destino.getId());

		mockMvc.perform(
				post("/api/v1/beneficios/transfer").contentType(MediaType.APPLICATION_JSON).content(transferenciaJson))
				.andExpect(status().isOk());

		BeneficioDTO origemAtualizada = beneficioService.buscarPorId(origem.getId());
		BeneficioDTO destinoAtualizado = beneficioService.buscarPorId(destino.getId());

		assertEquals(new BigDecimal("900.00"), origemAtualizada.getValor());
		assertEquals(new BigDecimal("600.00"), destinoAtualizado.getValor());
	}

	@Test
	void testTransferir_SaldoInsuficiente() throws Exception {
		BeneficioDTO origem = beneficioService.criar(beneficio2);
		BeneficioDTO destino = beneficioService.criar(beneficio1);

		String transferenciaJson = String.format("{\"fromId\": %d, \"toId\": %d, \"amount\": 1000.00}", origem.getId(),
				destino.getId());

		mockMvc.perform(
				post("/api/v1/beneficios/transfer").contentType(MediaType.APPLICATION_JSON).content(transferenciaJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testTransferir_BeneficioInativo() throws Exception {
		BeneficioDTO origem = beneficioService.criar(beneficio1);
		BeneficioDTO destino = beneficioService.criar(beneficio2);
		destino.setAtivo(false);
		beneficioService.atualizar(destino.getId(), destino);

		String transferenciaJson = String.format("{\"fromId\": %d, \"toId\": %d, \"amount\": 100.00}", origem.getId(),
				destino.getId());

		mockMvc.perform(
				post("/api/v1/beneficios/transfer").contentType(MediaType.APPLICATION_JSON).content(transferenciaJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testTransferir_MesmoBeneficio() throws Exception {
		BeneficioDTO beneficio = beneficioService.criar(beneficio1);

		String transferenciaJson = String.format("{\"fromId\": %d, \"toId\": %d, \"amount\": 100.00}",
				beneficio.getId(), beneficio.getId());

		mockMvc.perform(
				post("/api/v1/beneficios/transfer").contentType(MediaType.APPLICATION_JSON).content(transferenciaJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCriarBeneficio_ValidacaoNomeObrigatorio() throws Exception {
		beneficio1.setNome("");
		String json = objectMapper.writeValueAsString(beneficio1);

		mockMvc.perform(post("/api/v1/beneficios").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCriarBeneficio_ValidacaoValorObrigatorio() throws Exception {
		beneficio1.setValor(null);
		String json = objectMapper.writeValueAsString(beneficio1);

		mockMvc.perform(post("/api/v1/beneficios").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testCriarBeneficio_ValidacaoValorMaiorQueZero() throws Exception {
		beneficio1.setValor(BigDecimal.ZERO);
		String json = objectMapper.writeValueAsString(beneficio1);

		mockMvc.perform(post("/api/v1/beneficios").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
}
