package com.fzenner.datademo.web.outmsg;

public class ErrorInfoExtern {

	public String errorCode;
	public String errorText;
	
	public ErrorInfoExtern() {
		
	}
	
	public ErrorInfoExtern(String errorCode, String errorNumber) {
		super();
		this.errorCode = errorCode;
		this.errorText = errorNumber;
	}
	
	@Override
	public String toString() {
		return String.format("ErrorCode: %s, errorText: %s", errorCode, errorText);
	}
	
}
