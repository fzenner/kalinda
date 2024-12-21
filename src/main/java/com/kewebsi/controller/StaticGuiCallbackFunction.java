package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.HtmlPage;

@FunctionalInterface
public interface StaticGuiCallbackFunction {
	abstract MsgAjaxResponse handleGuiMsg(JsonNode rootNode , UserSession userSession, HtmlPage htmlPage);
}
