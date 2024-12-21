package com.kewebsi.html;

@FunctionalInterface
public interface StringModelGetter<S, M> {  // State and Model as Generic Type Parameter
	M get(S pageState);
}