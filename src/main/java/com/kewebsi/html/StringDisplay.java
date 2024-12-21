package com.kewebsi.html;

import com.kewebsi.util.CommonUtils;

public abstract class StringDisplay extends HtmlTag {

	protected String oldValue;
	
	abstract public String getValue();
	
	@Override
	public boolean isContentOrGuiDefModified() {
		if (CommonUtils.equals(getValue(), oldValue)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setContentOrGuiDefNotModified() {
		oldValue = getValue();
	}



	@Override
	public String toString() {
		return "id:" + id + " value:" + getValue() + " oldValue:" + oldValue;
	}
	
	
	
	
	
}
