package com.kewebsi.html;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.util.JsonUtils;

public class HtmlDiv extends HtmlTag {
	
	public final static String cssScrollOverflow = "scrollOverflow";   // in css: .div.scroll;Oberflow
	public final static String cssVertikalInputFieldGrid2Cols = "divVertikalInputFieldGrid2Cols";

	// protected String myClass;
	
	protected final String formatHtml = "<div class=\"%s\">%s</div>\n";
	protected final String formatString = "<div class=\"%s\">";

	protected int modificationCount = 0;

	public HtmlDiv() {
	}
	
	public HtmlDiv(String myClass) {
		addCssClass(myClass);
	}

	
	public HtmlDiv(String myClass, HtmlTag child) {
		addCssClass(myClass);
		addChild(child);
	}

	public HtmlDiv(String id, String myClass) {
		super(id);
		addCssClass(myClass);
	}
	
	public HtmlDiv(String id, String myClass, HtmlTag child) {
		super(id);
		addCssClass(myClass);
		this.addChild(child);
	}
	
	
	public <S, T extends Enum<T>> HtmlDiv addLabelAndTag(String labelText, HtmlTag tagToAdd) {
		HtmlLabel label = new HtmlLabel(labelText);
		label.setForTag(tagToAdd);
		label.addCssClass(CssConstants.cssInputFieldLabelLeft);
		addChild(label);
		addChild(tagToAdd);
		return this;
	}
	

	@Override
	public GuiDef getGuiDef() {
		GuiDef result = new GuiDef("div" , id);
		result.cssClasses = this.getCssClasses();
		result.appendChildren(getChildren());
		return result;
	}


	@Override
	public String toString() {
		String myClasses = this.getCssClassesAsString();
		return String.format(formatString, myClasses);
	}
	

	public void touch() {
		modificationCount++;
	}

	@Override
	public boolean isContentOrGuiDefModified() {
		return modificationCount>0;
	}

	@Override
	public void setContentOrGuiDefNotModified() {
		modificationCount = 0;
	}
	
	
}
