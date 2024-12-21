package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.HtmlPage;

/**
 * GUI delegates proved a link between their string-based id (for referencing in html) and a lambda in Java.
 * @author A4328059
 *
 */
public interface GuiDelegate {

	public MsgAjaxResponse handleGuiMsg(JsonNode rootNode , UserSession userSession, HtmlPage htmlPage);
	public String getCallbackId();
	
}
