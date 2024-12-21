package com.kewebsi.html;

import com.kewebsi.controller.GuiDelegate;

public class HtmlTagClientServerActionText extends HtmlTagClientServerAction {


	public HtmlTagClientServerActionText() {

	}

	public HtmlTagClientServerActionText(String tag, boolean hasClosingTag, String id, String clientEventHandler, GuiDelegate serverCallback, String... constParamsKeyValue) {
		initTag(tag, hasClosingTag, id, clientEventHandler, serverCallback, constParamsKeyValue);
	}

//	@Override
//	public String getChildrenHtml() {
//		return contentAsString;
//	}

	@Override
	public boolean hasClosingTag() {
		return true;
	}
	
}
