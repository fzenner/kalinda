package com.kewebsi.html;

/**
 * This is the same as Function<T,R>.
 * We provide this interface in order to be able to indicate that we apply a getter of an object
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface AnyGetter<T, R> {
    R get(T t);
}

