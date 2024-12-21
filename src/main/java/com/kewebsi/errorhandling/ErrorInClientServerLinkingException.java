package com.kewebsi.errorhandling;

public class ErrorInClientServerLinkingException extends RuntimeException {


	private static final long serialVersionUID = -4733082668277274364L;

	public ErrorInClientServerLinkingException() {
		super();
	}

	public ErrorInClientServerLinkingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ErrorInClientServerLinkingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ErrorInClientServerLinkingException(String message) {
		super(message);
	}

	public ErrorInClientServerLinkingException(Throwable cause) {
		super(cause);
	}
	
}

