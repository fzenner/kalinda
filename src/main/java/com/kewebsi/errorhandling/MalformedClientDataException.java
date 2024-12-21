package com.kewebsi.errorhandling;


public class MalformedClientDataException extends RuntimeException {

	private static final long serialVersionUID = -3572591263295844266L;
	
	public MalformedClientDataException() {
		super();
	}

	public MalformedClientDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MalformedClientDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedClientDataException(String message) {
		super(message);
	}

	public MalformedClientDataException(Throwable cause) {
		super(cause);
	}

}