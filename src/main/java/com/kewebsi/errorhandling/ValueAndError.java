package com.kewebsi.errorhandling;



public class ValueAndError<T> {
	public T value;
	public ErrorInfo errorInfo;
	
	
	public boolean isNull() {
		return value == null;
	}
	
	public ValueAndError(T value) {
		this.value = value;
	}

	public ValueAndError(T value, ErrorInfo errorInfo) {
		this.value = value;
		this.errorInfo = errorInfo;
	}
	
	public ValueAndError(Enum<?> errorCode) {
		errorInfo = new ErrorInfo(errorCode, "");
	}	
	
	public ValueAndError(Enum<?> errorCode, String errorText) {
		this.errorInfo = new ErrorInfo(errorCode, errorText);
	}
	
	
	public ValueAndError(ValueAndError<?> otherNullInfoObj) {
		this.errorInfo = otherNullInfoObj.getErrorInfo();
	}
	

	public T getValue() {
		if (errorInfo != null) {
			throw new CodingErrorException("Attempt to get value from error");
		}
		return value;
	}

	public T getValueEvenIfErrorPresent() {
		return value;
	}
	
	public boolean hasValue() {
		if (value != null) {
			return true;
		} 
		return false;
	}
	
	
	public boolean hasError() {
		if (errorInfo != null) {
			return true;
		} 
		return false;
	}


	public Enum<?> getErrorCode() {
		return errorInfo!=null ? errorInfo.errorCode : null;
	}


	public ErrorInfo getErrorInfo() {
		return errorInfo;
	}
	
	public String getErrorText() {
		String result = "NO ERROR TEXT AVAILABLE";
		if (errorInfo != null) {
			if (errorInfo.errorText != null) {
				result = errorInfo.errorText;
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return getErrorText();
	}
	
	

//	public void setErrorInfo(ErrorInfo errorInfo) {
//		this.errorInfo = errorInfo;
//	}
	
}	
