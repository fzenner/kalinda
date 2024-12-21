package com.kewebsi.errorhandling;

import com.fzenner.datademo.web.outmsg.MsgErrorInfo.ErrorCodes;

// @SuppressWarnings("rawtypes")   // We do not want to use generics with enums. It seems to be overkill. For error handling purposes it is enough to know that it is an enum, 
                                 // since we are only interested in the string (tag). It is within the responsibility of the creators of error enums 
                                // to make sure that they are sufficiently unambiguous.
public class ErrorInfo {

	public String wireNull = null;

	enum StandardErrorCodes {INFO_IN_TEXT_ONLY, CLEAR_ERROR};

	private static ErrorInfo clearError = null;

	protected Enum<?> errorCode;
	protected String errorText;


	public ErrorInfo(String errorText) {
		this.errorCode = StandardErrorCodes.INFO_IN_TEXT_ONLY;
		this.errorText = errorText;
	}



	public ErrorInfo(Enum<?> errorCode, String errorText) {
		this.errorCode = errorCode;
		this.errorText = errorText;
	}

	/**
	 * Indicates that there is no error. To be used for client-server communication via JSON, where null fields
	 * would not be transferred. Used by the excplict creator function below.
	 */
	private ErrorInfo() {
		wireNull = "true";
		this.errorCode = null;
		this.errorText = null;

	}
	
	
	public Enum<?> getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCodes errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	/**
	 * Indicates that there is no error. To be used for client-server communication via JSON, where null fields
	 * would not be transferred.
	 */
	public static ErrorInfo wireNull() {
		return new ErrorInfo();
	}

	
}
