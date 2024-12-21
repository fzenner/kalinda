package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.HtmlPage;

@FunctionalInterface
public interface DynamicGuiCallbackFunction<T> {
	abstract MsgAjaxResponse callbackLambda(T objectToInvoke, JsonNode rootNode , UserSession userSession, HtmlPage page);
}


