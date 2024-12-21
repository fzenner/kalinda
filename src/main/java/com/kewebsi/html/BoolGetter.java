package com.kewebsi.html;


@FunctionalInterface
public interface BoolGetter<S> {
	boolean get(S pageState);
}

