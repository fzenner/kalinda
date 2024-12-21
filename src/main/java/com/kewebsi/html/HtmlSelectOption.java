package com.kewebsi.html;

import java.util.ArrayList;

import com.kewebsi.util.EnumUtils;

public class HtmlSelectOption {

	protected String value;
	protected String  text;
	
	public HtmlSelectOption(String value, String text) {
		super();
		setValue(value);
		setText(text);
	}
	
	
	public <T extends Enum<T>> HtmlSelectOption(T value, String text) {
		super();
		setValue(value.name());
		setText(text);
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value == null ? "" : value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text == null ? "" : text;
	}
	
	public static <T extends Enum<T>> ArrayList<HtmlSelectOption> createDefaultOptionList(Class<T> c) {
		String[] enumStrs = EnumUtils.enumToStringArray(c);
		var result = new  ArrayList<HtmlSelectOption>(enumStrs.length);
		for (String sRun : enumStrs) {
			result.add(new HtmlSelectOption(sRun,sRun));
		}
		return result;
	}
	
	
}
