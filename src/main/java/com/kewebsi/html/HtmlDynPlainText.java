package com.kewebsi.html;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

public class HtmlDynPlainText extends StringInputFieldByPageState {
	
	public static final String format = "<span id=\"%s\">%s</span>";

	public <S> HtmlDynPlainText(StringGetter<S> stringGetter, String id) {
		this.stringGetter = stringGetter;
		this.id = id;
	}
	
	public <T extends Enum<T>, S> HtmlDynPlainText(StringGetter<S> stringGetter, T id) {
		this.stringGetter = stringGetter;
		this.id = id.name();
	}

}