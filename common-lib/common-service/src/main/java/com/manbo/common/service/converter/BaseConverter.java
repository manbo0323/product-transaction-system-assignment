package com.manbo.common.service.converter;

public interface BaseConverter<DO, DTO> {

    DO toDO(DTO dto);

    DTO toDTO(DO dto);
}
