package com.kewebsi.html;

public class InputParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2905974520314002139L;

	public InputParsingException() {
	}

	public InputParsingException(String message) {
		super(message);
	}

	public InputParsingException(Throwable cause) {
		super(cause);
	}

	public InputParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public InputParsingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
