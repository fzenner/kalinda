package com.kewebsi.html;

import com.kewebsi.controller.GuiDelegate;

public abstract class StringInputFieldByPageState extends StringDisplay {

	protected  StringGetter stringGetter;
	protected GuiDelegate serverMsgHandler;
	
	protected String name;

	@Override
	public String getValue() {
		PageState pageState = getPageState();
		if (pageState == null) {
			return "";
		}
		return stringGetter.get(pageState);  // Wird umgewandelt in pageState.accessorAsSpecifiedDuringConstruction()
	}

	
	public StringInputFieldByPageState linkCallback(GuiDelegate serverMsgHandler) {
		this.serverMsgHandler = serverMsgHandler;
		return this;
	}
	
	
	public String getName() {
		return name;
	}



	@Override
	public String toString() {
		return "id:" + id + " name:" + name + " value:" + getValue() + " oldValue:" + oldValue;
	}
	
	
	
	
}
