package com.example.beneficio.domain.port.output;

import java.util.List;
import java.util.Optional;

import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.model.Pageable;

public interface BeneficioRepository {

	Optional<Beneficio> buscarPorId(Long id);

	Optional<Beneficio> buscarPorIdComLock(Long id);

	Page<Beneficio> listarTodos(Pageable pageable);

	List<Beneficio> buscarAtivosPorNome(String nome);

	Beneficio salvar(Beneficio beneficio);

	void remover(Long id);
}
