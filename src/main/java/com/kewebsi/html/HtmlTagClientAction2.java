package com.kewebsi.html;

import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.kewebsi.util.JsonUtils;

public abstract class HtmlTagClientAction2 extends ConstantHtmlTag{
	protected String tagName;
	protected String label;
	protected String clientEventHandler;

	protected HtmlTagClientAction2() {
		
	}
	


	protected void init(String tagName, String id, String label, String clientEventHandler) {
		setId(id);
		this.tagName = tagName;
		this.label = label;
		this.clientEventHandler = clientEventHandler;
	}
	
	public String getLabel() {
		return label;
	}

	public abstract String getParamsAsHtmlAttributes();
	

}
