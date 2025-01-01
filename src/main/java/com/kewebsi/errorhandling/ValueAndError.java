package com.kewebsi.errorhandling;



public class ValueAndError<T> {
	public T value;
	public ErrorUpdate errorUpdate;
	
	
	public boolean isNull() {
		return value == null;
	}
	
	public ValueAndError(T value) {
		this.value = value;
	}

	public ValueAndError(T value, ErrorUpdate errorUpdate) {
		this.value = value;
		this.errorUpdate = errorUpdate;
	}
	
	public ValueAndError(Enum<?> errorCode) {
		errorUpdate = new ErrorUpdate(errorCode, "");
	}	
	
	public ValueAndError(Enum<?> errorCode, String errorText) {
		this.errorUpdate = new ErrorUpdate(errorCode, errorText);
	}
	
	
	public ValueAndError(ValueAndError<?> otherNullInfoObj) {
		this.errorUpdate = otherNullInfoObj.getErrorInfo();
	}
	

	public T getValue() {
		if (errorUpdate != null) {
			throw new CodingErrorException("Attempt to get newValue from error");
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
		if (errorUpdate != null) {
			return true;
		} 
		return false;
	}


	public Enum<?> getErrorCode() {
		return errorUpdate !=null ? errorUpdate.errorCode : null;
	}


	public ErrorUpdate getErrorInfo() {
		return errorUpdate;
	}
	
	public String getErrorText() {
		String result = "NO ERROR TEXT AVAILABLE";
		if (errorUpdate != null) {
			if (errorUpdate.errorText != null) {
				result = errorUpdate.errorText;
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
