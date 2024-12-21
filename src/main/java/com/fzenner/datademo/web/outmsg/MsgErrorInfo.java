package com.fzenner.datademo.web.outmsg;

import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.errorhandling.ValueAndError;


public class MsgErrorInfo extends MsgJsonOut{
	protected String errorText;
	
	public enum ErrorCodes {
		UNSPECIFIED,
		TABLE_ALREADY_EXISTS,
		PLAYER_NOT_LOGGED_IN,
		PLAYER_ALREADY_ON_TABLE,
		ROLE_NOT_AVAILABLE,
		PLAYER_ALREADY_NOT_ON_TABLE,
		MAX_CONNECTIONS_PER_PLAYER_AND_TABLE_EXCEEDED,
		TABLE_FULL,
		TABLE_NAME_TOO_SHORT,
		TABLE_NOT_FOUND,
		PLAYER_NAME_TOO_SHORT,
		ROLE_INVALID,
		ROLE_EMPTY
	}
	
	Enum<?> errorCode;
	
	public MsgErrorInfo(String errorText) {
		super(MsgKewebsiOut.ERROR_INFO);
		this.errorText = errorText;
		this.errorCode = ErrorCodes.UNSPECIFIED;
	}
	
	
	public MsgErrorInfo(String errorText, ErrorCodes errorCode) {
		super(MsgKewebsiOut.ERROR_INFO);
		this.errorText = errorText;
		this.errorCode = errorCode;
	}
	
	public MsgErrorInfo(ErrorInfo errorInfo) {
		super(MsgKewebsiOut.ERROR_INFO);
		this.errorText = errorInfo.getErrorText();
		this.errorCode = errorInfo.getErrorCode();
	}
	
	public MsgErrorInfo(ValueAndError<?> valueAndError) {
		super(MsgKewebsiOut.ERROR_INFO);
		if (valueAndError.hasValue()) {
			throw new  CodingErrorException("Attempt to generate an error message when no error has occurred.");
		}
		ErrorInfo errorInfo = valueAndError.getErrorInfo();
		this.errorText = errorInfo.getErrorText();
		this.errorCode = errorInfo.getErrorCode();
	}
	
	@Override
	public String toString() {
		return String.format("ErrorCode: %s ErrorText: %s", errorCode.toString(), errorText);
	}


	public String getErrorText() {
		return errorText;
	}


	public Enum<?> getErrorCode() {
		return errorCode;
	}
	
	
	
}
