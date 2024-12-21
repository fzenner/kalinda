package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.StandardController;

public abstract class HtmlDivButtonStandard extends SmartTag implements ButtonClickHandler {

	protected static final String CLIENT_EVENT_HANDLER = "CEH_standardButtonClicked";

	public HtmlDivButtonStandard(String id, String label) {
		setId(id);
		setText(label);
		setClientEventHandler(ClientEventHandler.CEH_standardButtonClicked);
	}

	@Override
	public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
		return null;
	}

	@Override
	public String getTag() {
		return "div";
	}

	@Override
	public GuiDef getGuiDef() {
		return super.getGuiDef();
	}
}
