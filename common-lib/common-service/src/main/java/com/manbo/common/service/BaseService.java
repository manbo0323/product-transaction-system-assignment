package com.manbo.common.service;

import com.manbo.common.service.converter.BaseConverter;
import com.manbo.common.util.dto.PageDTO;
import org.springframework.data.domain.Page;

import java.util.function.Function;

/**
 * @author Manbo
 */
public abstract class BaseService {

    protected <DO, DTO> PageDTO<DTO> convert(Page<DO> source, BaseConverter<DO, DTO> converter) {
        return PageDTO.<DTO>builder()
            .list(source.get().map(converter::toDTO).toList())
            .pageNumber(source.getNumber())
            .pageSize(source.getSize())
            .totalCount(source.getTotalElements()).build();
    }

    protected <T> PageDTO<T> convert(Page<T> source) {
        return PageDTO.<T>builder().list(source.getContent())
            .pageNumber(source.getNumber())
            .pageSize(source.getSize())
            .totalCount(source.getTotalElements()).build();
    }

    protected <S, T> PageDTO<T> convert(Page<S> source, Function<S, T> function) {
        return PageDTO.<T>builder()
            .list(source.get().map(function).toList())
            .pageNumber(source.getNumber())
            .pageSize(source.getSize())
            .totalCount(source.getTotalElements()).build();
    }

}
