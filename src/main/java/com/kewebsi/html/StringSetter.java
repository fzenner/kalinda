package com.kewebsi.html;


@FunctionalInterface
public interface StringSetter<S> {
	String set(S pageState, String s);
}
