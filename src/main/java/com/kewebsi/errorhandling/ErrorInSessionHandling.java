package com.kewebsi.errorhandling;



public class ErrorInSessionHandling extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ErrorInSessionHandling() {
		super();
	}

	public ErrorInSessionHandling(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ErrorInSessionHandling(String message, Throwable cause) {
		super(message, cause);
	}

	public ErrorInSessionHandling(String message) {
		super(message);
	}

	public ErrorInSessionHandling(Throwable cause) {
		super(cause);
	}

}