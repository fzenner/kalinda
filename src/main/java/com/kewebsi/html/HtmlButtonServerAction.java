package com.kewebsi.html;

import com.kewebsi.controller.GuiDelegate;

public class HtmlButtonServerAction extends HtmlTagClientServerActionText {

	protected static final String CLIENT_EVENT_HANDLER = "CEH_genericDataSubmit";

	public HtmlButtonServerAction() {

	}

	public HtmlButtonServerAction(String id, String label, GuiDelegate serverCallback, String... constParamsKeyValue) {
		// addChild(new HtmlPlainText(label));
		setText(label);
		initTag("button", true, id, CLIENT_EVENT_HANDLER, serverCallback, constParamsKeyValue);
	}

	
	public HtmlButtonServerAction initButton(String id, String label,  GuiDelegate serverCallback, String... constParamsKeyValue) {
		// addChild(new HtmlPlainText(label));
		setText(label);
		initTag("button", true,id, CLIENT_EVENT_HANDLER, serverCallback, constParamsKeyValue);
		return this;
	}


	@Override
	public HtmlButtonServerAction putNonStandardAttribute(String key, String value) {
		super.putNonStandardAttribute(key, value);
		return this;
	}

//	@Override
//	public HtmlButtonServerAction addGenericAttributes(String... keyValues) {
//		super.addGenericAttributes(keyValues);
//		return this;
//	}
}
