package com.kewebsi.html;



@FunctionalInterface
public interface ColWidthMapper<T extends Enum<T>> {
	int getWidth(T en);
}
