package com.kewebsi.html;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.util.PageBuilder;

public class HtmlInput extends StringInputFieldByPageState {

	
	protected String placeholder = "";
	
	public boolean readOnly;

	public HtmlInput(StringGetter stringGetter, String id) {
		this.stringGetter = stringGetter;
		this.id = id;
		this.name = id;
	}
	
	
	public <T extends Enum<T>, S> HtmlInput(StringGetter<S> stringGetter, T  id) {
		this.stringGetter = stringGetter;
		this.id = id.name();
		this.name = this.id;;
	}
	
	
	
	public HtmlInput setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}
	

	public String getPlaceholder() {
		return placeholder;
	}


	public HtmlInput setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		return this;
	}
	

}