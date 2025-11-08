package com.example.backend.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.beneficio.domain.model.Beneficio;

@Mapper(componentModel = "spring")
public interface BeneficioDTOMapper {

	@Mapping(target = "id", source = "id")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "descricao", source = "descricao")
	@Mapping(target = "valor", source = "valor")
	@Mapping(target = "ativo", source = "ativo")
	@Mapping(target = "version", source = "version")
	BeneficioDTO toDTO(Beneficio beneficio);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "descricao", source = "descricao")
	@Mapping(target = "valor", source = "valor")
	@Mapping(target = "ativo", source = "ativo")
	@Mapping(target = "version", source = "version")
	Beneficio toDomain(BeneficioDTO dto);
}
