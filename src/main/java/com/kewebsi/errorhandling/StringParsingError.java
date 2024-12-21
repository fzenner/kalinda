package com.kewebsi.errorhandling;


public class StringParsingError extends Exception {

	protected String badValue;


	public StringParsingError() {
		super();
	}

	public StringParsingError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StringParsingError(String message, Throwable cause) {
		super(message, cause);
	}

	public StringParsingError(String message, String badValue) {
		super(message);
		this.badValue = badValue;
	}

	public StringParsingError(Throwable cause) {
		super(cause);
	}

	public String getBadValue() {
		return badValue;
	}

	public void setBadValue(String badValue) {
		this.badValue = badValue;
	}

}