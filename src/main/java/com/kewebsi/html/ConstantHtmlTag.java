package com.kewebsi.html;

public abstract class ConstantHtmlTag extends HtmlTag {

	
	public ConstantHtmlTag() {
	}
	
	public ConstantHtmlTag(String id) {
		super(id);
	}

	@Override
	public boolean isContentOrGuiDefModified() {
		return false;
	}

	@Override
	public void setContentOrGuiDefNotModified() {
	}

}
