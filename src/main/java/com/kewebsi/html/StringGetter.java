package com.kewebsi.html;

@FunctionalInterface
public interface StringGetter<S> {   // TODO PropertyGetter
	String get(S pageState);
}
