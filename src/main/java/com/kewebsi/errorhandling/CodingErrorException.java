package com.kewebsi.errorhandling;

/*
* Indicates a low-level implementation error.
* @author Master
*
*/
public class CodingErrorException extends RuntimeException {

	private static final long serialVersionUID = -3572591263295844266L;
	
	public CodingErrorException() {
		super();
	}

	public CodingErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CodingErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodingErrorException(String message) {
		super(message);
	}

	public CodingErrorException(Throwable cause) {
		super(cause);
	}

}