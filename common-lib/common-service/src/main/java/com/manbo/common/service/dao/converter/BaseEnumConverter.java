package com.manbo.common.service.dao.converter;

import com.manbo.common.util.enums.DatabaseEnum;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author kai
 */
@Slf4j
public class BaseEnumConverter<E extends DatabaseEnum<T>, T> implements AttributeConverter<E, T> {

    @Override
    public final T convertToDatabaseColumn(final E enumValue) {
        return Optional.ofNullable(enumValue).map(DatabaseEnum::getValue).orElse(null);
    }

    @Override
    public final E convertToEntityAttribute(final T dbValue) {
        return Optional.ofNullable(dbValue).flatMap(this::toEnum).orElse(null);
    }

    private Optional<E> toEnum(final T dbValue) {
        try {
            final Type genericSuperclass = getClass().getGenericSuperclass();
            if(genericSuperclass instanceof ParameterizedType parameterizedType) {
                final Class<E> enumClass = (Class<E>) parameterizedType.getActualTypeArguments()[0];
                final Class<T> valueClass = (Class<T>) parameterizedType.getActualTypeArguments()[1];
                return (Optional<E>) enumClass.getDeclaredMethod("lookup", valueClass).invoke(null, dbValue);
            }
        } catch (Exception e) {
            log.error("[EnumConverter] try convert db value to enum got error!", e);
        }
        return Optional.empty();
    }

}
