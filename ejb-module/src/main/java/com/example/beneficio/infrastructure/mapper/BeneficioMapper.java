package com.example.beneficio.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.infrastructure.persistence.entity.BeneficioEntity;

@Mapper(componentModel = "default")
public interface BeneficioMapper {

	@Mapping(target = "id", source = "id")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "descricao", source = "descricao")
	@Mapping(target = "valor", source = "valor")
	@Mapping(target = "ativo", source = "ativo")
	@Mapping(target = "version", source = "version")
	Beneficio toDomain(BeneficioEntity entity);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "descricao", source = "descricao")
	@Mapping(target = "valor", source = "valor")
	@Mapping(target = "ativo", source = "ativo")
	@Mapping(target = "version", source = "version")
	BeneficioEntity toEntity(Beneficio domain);
}
