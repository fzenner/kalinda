package com.fzenner.datademo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public class TestController {

	protected String eatMe;
	
	public TestController(String eatMe) {
		this.eatMe = eatMe;
		
	}
	
	public MsgAjaxResponse helloWorld(JsonNode rootNode, UserSession userSession) {
		System.out.println(eatMe);
		return MsgAjaxResponse.createSuccessMsg();
	}
	
	
	public static MsgAjaxResponse helloWorld2(TestController c, JsonNode rootNode, UserSession userSession) {
		return MsgAjaxResponse.createSuccessMsg();
	}
	
	
}
