package com.kewebsi.html;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.controller.SearchDivController;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

import java.util.LinkedHashMap;

public class HtmlTagClientAction extends ConstantHtmlTag{
	protected String tagName;
	protected String label;
	protected String clientEvent_1;
	protected String clientEvent_2;
	protected String clientEvent_3;
	protected String clientEventHandler_1;
	protected String clientEventHandler_2;
	protected String clientEventHandler_3;
	protected String[] constParamsKeyValue;

	protected HtmlTagClientAction() {
		
	}
	


	protected void init(String tagName, String id, String label, String clientEventHandler, 
			String... constParamsKeyValue) { 
		setId(id);
		this.tagName = tagName;
		this.label = label;
		this.clientEvent_1 = "click";
		this.clientEventHandler_1 = clientEventHandler;
		this.constParamsKeyValue = constParamsKeyValue;
	}

	public void setEventHandler_2(String clientEvent_2, String clientEventHandler_2) {
		this.clientEvent_2 = clientEvent_2;
		this.clientEventHandler_2 = clientEventHandler_2;
	}

	public void setEventHandler_3(String clientEvent_3, String clientEventHandler_3) {
		this.clientEvent_3 = clientEvent_3;
		this.clientEventHandler_3 = clientEventHandler_3;
	}

	@Override
	public GuiDef getGuiDef() {
		GuiDef result = new GuiDef(tagName, id);
		result.cssClasses = this.getCssClasses();
		result.text = label;

		result.clientEventHandler = clientEventHandler_1;
		if (constParamsKeyValue != null && constParamsKeyValue.length > 0) {
			result.attrs = new LinkedHashMap<>(constParamsKeyValue.length / 2);
			CommonUtils.addAttributes(result.attrs, constParamsKeyValue);
		}

		return result;
	}


	public String getLabel() {
		return label;
	}
	

}
