package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.StandardController;

public abstract class HtmlButtonStandard extends SmartTag implements ButtonClickHandler {

	public HtmlButtonStandard(String id, String label) {
		setId(id);
		setText(label);
	}

	@Override
	public String getTag() {
		return "standardbutton";
	}


}
