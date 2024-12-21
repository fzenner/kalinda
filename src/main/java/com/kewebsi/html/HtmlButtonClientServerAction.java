package com.kewebsi.html;

import com.kewebsi.controller.GuiDelegate;

public class HtmlButtonClientServerAction extends HtmlTagClientServerActionText {


	public HtmlButtonClientServerAction() {
		
	}
	
	public HtmlButtonClientServerAction(String id, String label, String clientEventHandler, GuiDelegate serverCallback, String... constParamsKeyValue) {	
		initTag("button", true, id, clientEventHandler, serverCallback, constParamsKeyValue);
		setText(label);
		// addChild(new HtmlPlainText(label));
	}

	
	public HtmlButtonClientServerAction initButton(String id, String label, String clientEventHandler, GuiDelegate serverCallback, String... constParamsKeyValue) {
		initTag("button", true,id, clientEventHandler, serverCallback, constParamsKeyValue);
		setText(label);
		// addChild(new HtmlPlainText(label));
		return this;
	}


	@Override
	public HtmlButtonClientServerAction putNonStandardAttribute(String key, String value) {
		super.putNonStandardAttribute(key, value);
		return this;
	}

//	@Override
//	public HtmlButtonClientServerAction addGenericAttributes(String... keyValues) {
//		super.addGenericAttributes(keyValues);
//		return this;
//	}
}
