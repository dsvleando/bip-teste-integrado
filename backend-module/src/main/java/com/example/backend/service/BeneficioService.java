package com.example.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.api.dto.BeneficioSearchDTO;
import com.example.backend.api.dto.PageDTO;
import com.example.backend.api.dto.TransferenciaDTO;
import com.example.backend.api.mapper.BeneficioDTOMapper;
import com.example.backend.api.mapper.BeneficioSearchDTOMapper;
import com.example.beneficio.domain.exception.BeneficioException;
import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.model.Pageable;
import com.example.beneficio.domain.port.input.BeneficioUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficioService {

	private final BeneficioUseCase beneficioUseCase;
	private final BeneficioDTOMapper mapper;
	private final BeneficioSearchDTOMapper searchMapper;

	public PageDTO<BeneficioDTO> listarTodos(org.springframework.data.domain.Pageable pageable) {
		Pageable domainPageable = Pageable.of(pageable.getPageNumber(), pageable.getPageSize());
		Page<Beneficio> page = beneficioUseCase.listarTodos(domainPageable);

		List<BeneficioDTO> content = page.getContent().stream().map(mapper::toDTO).collect(Collectors.toList());

		return PageDTO.<BeneficioDTO>builder().content(content).page(page.getPage()).size(page.getSize())
				.totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).first(page.isFirst())
				.last(page.isLast()).hasNext(page.hasNext()).hasPrevious(page.hasPrevious()).build();
	}

	public BeneficioDTO buscarPorId(Long id) {
		Beneficio beneficio = beneficioUseCase.buscarPorId(id)
				.orElseThrow(() -> new BeneficioException("Benefício não encontrado: " + id));
		return mapper.toDTO(beneficio);
	}

	public BeneficioDTO criar(BeneficioDTO dto) {
		return mapper.toDTO(beneficioUseCase.criar(mapper.toDomain(dto)));
	}

	public BeneficioDTO atualizar(Long id, BeneficioDTO dto) {
		return mapper.toDTO(beneficioUseCase.atualizar(id, mapper.toDomain(dto)));
	}

	public void remover(Long id) {
		beneficioUseCase.remover(id);
	}

	public void transferir(TransferenciaDTO dto) {
		beneficioUseCase.transferir(dto.getFromId(), dto.getToId(), dto.getAmount());
	}

	public List<BeneficioSearchDTO> buscarAtivosPorNome(String nome) {
		return beneficioUseCase.buscarAtivosPorNome(nome).stream().map(searchMapper::toSearchDTO)
				.collect(Collectors.toList());
	}
}
