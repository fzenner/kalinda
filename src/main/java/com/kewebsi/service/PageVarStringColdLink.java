package com.kewebsi.service;

import com.kewebsi.controller.BaseVal;
import com.kewebsi.controller.BaseValString;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.html.PageState;

public class PageVarStringColdLink extends PageStateVarColdLink<String> {

	public PageVarStringColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {
		super(pageState, fieldAssistant, fieldPrefix);
	}

	public PageVarStringColdLink(PageState pageState, FieldAssistant fieldAssistant) {
		super(pageState, fieldAssistant);
	}


	public PageVarStringColdLink(String htmlId, PageState pageState) {
		super(htmlId, pageState);
	}



	@Override
	public String getValAsString() {     // TODO Escape for HTML display.
		return val;
	}

	@Override
	public PageVarError validateUnparsedStringValueAndSetValueOrErrorAllowNull() {
		this.val = unparsedStringValue.trim();
		clearError();
		unparsedStringValue = null;
		return null;
	}

//	@Override
//	public String parseNonEmptyStringInput(String clientInput) throws PageVarError {
//		XXXXXXXXXXX return null;
//	}


	@Override
	public void setValueFromBackend(BaseVal baseVal) {
		assert(baseVal.getType() == FieldAssistant.FieldType.STR);
		val = ((BaseValString) baseVal).getVal();
		unparsedStringValue = null;
	}


}
