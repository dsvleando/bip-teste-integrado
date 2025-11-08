package com.example.backend.api.mapper;

import org.mapstruct.Mapper;

import com.example.backend.api.dto.BeneficioSearchDTO;
import com.example.beneficio.domain.model.Beneficio;

@Mapper(componentModel = "spring")
public interface BeneficioSearchDTOMapper {

	BeneficioSearchDTO toSearchDTO(Beneficio beneficio);
}

