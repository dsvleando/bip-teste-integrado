package com.example.beneficio.domain.port.input;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.model.Pageable;

public interface BeneficioUseCase {

	Page<Beneficio> listarTodos(Pageable pageable);

	List<Beneficio> buscarAtivosPorNome(String nome);

	Optional<Beneficio> buscarPorId(Long id);

	Beneficio criar(Beneficio beneficio);

	Beneficio atualizar(Long id, Beneficio beneficio);

	void remover(Long id);

	void transferir(Long fromId, Long toId, BigDecimal amount);
}
