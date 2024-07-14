package com.manbo.common.util.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Manbo
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {

    public static boolean isEmpty(final Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(final Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> Stream<T> streamOf(final Iterable<T> iterable) {
        if (Objects.isNull(iterable)) {
            return Stream.empty();
        }
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> parallelStreamOf(final Iterable<T> iterable) {
        if (Objects.isNull(iterable)) {
            return Stream.empty();
        }
        return StreamSupport.stream(iterable.spliterator(), true);
    }

    public static <T> List<T> defaultList(final List<T> list) {
        if(Objects.isNull(list)) {
            return List.of();
        }
        return list;
    }
}
