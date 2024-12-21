package com.kewebsi.html;

@FunctionalInterface
public interface EntityGetter<E, P> {
    E get(P pageState);
}
