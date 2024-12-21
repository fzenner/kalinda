package com.kewebsi.errorhandling;


public class ExpectedClientDataError extends Exception {

	protected String badValue;

	private static final long serialVersionUID = -3572591263295844266L;

	public ExpectedClientDataError() {
		super();
	}

	public ExpectedClientDataError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpectedClientDataError(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpectedClientDataError(String message, String badValue) {
		super(message);
		this.badValue = badValue;
	}

	public ExpectedClientDataError(Throwable cause) {
		super(cause);
	}

	public String getBadValue() {
		return badValue;
	}

	public void setBadValue(String badValue) {
		this.badValue = badValue;
	}

}