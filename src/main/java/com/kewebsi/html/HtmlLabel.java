package com.kewebsi.html;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.controller.SearchDivController;
import com.kewebsi.util.JsonUtils;

public class HtmlLabel extends ConstantHtmlTag {

	protected String text;
	protected String forId;

	public HtmlLabel(String text) {
		this.text = text;
	}
	
	
	public HtmlLabel(String text, String forId) {
		this.text = text;
		this.forId = forId;
	}
	
	
	public HtmlLabel(String text, HtmlTag forTag) {
		this.text = text;
		this.forId = forTag.getId();
	}
	

	
	public static HtmlLabel byTextAndClass(String text, String cssClass) {
		HtmlLabel label = new HtmlLabel(text);
		label.addCssClass(cssClass);
		return label;
	}
	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getForId() {
		return forId;
	}
	
	
	public HtmlLabel setForTag(HtmlTag htmlTag) {
		forId = htmlTag.getId();
		return this;
	}

	@Override
	public GuiDef getGuiDef() {

		GuiDef result = new GuiDef("label" , id);
		result.text = text;
		return result;
	}


	public static String addTag(String tagName, String tagValue, String tagsSoFar) {
		String result = tagsSoFar;
		if (tagsSoFar.length() > 0) {
			result = " ";
		}
		if (tagValue != null) {
			result += tagName + "=\"" + tagValue + "\"";
		}
		return result;
	}

	// Override for specialized return parameter.
	@Override
	public HtmlLabel addCssClass(String cssClass) {
		super.addCssClass(cssClass);
		return this;
	}


	@Override
	public String toString() {
		return getText();
	}
	
	
	

}
