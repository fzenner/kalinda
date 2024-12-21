package com.kewebsi.util;

public class ErrorOrValue<E, V> {
	
	E error;
	V value;
	
	public E getError() {
		return error;
	}
	public void setError(E error) {
		this.error = error;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
	public boolean hasError() {
		if (error == null) {
			return false;
		} else {
			return true;
		}
	}

}
