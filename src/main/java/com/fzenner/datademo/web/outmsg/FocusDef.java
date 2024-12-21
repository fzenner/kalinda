package com.fzenner.datademo.web.outmsg;

import com.kewebsi.html.HtmlTag;

public class FocusDef {


    public static final String STANDARD_FOCUS_FUNCTION = "CFU_standardFocus";
    public static final String TABLE_FOCUS_FUNCTION = "CFU_tableFocus";
    public String focusFunction;
    public String tagIdToFocus;


    public FocusDef(HtmlTag tag, String focusFunction) {
        this.focusFunction = focusFunction;
        this.tagIdToFocus = tag.getId();
    }

    public FocusDef(String tagId, String focusFunction) {
        this.focusFunction = focusFunction;
        this.tagIdToFocus = tagId;
    }

    public FocusDef(String tagId) {
        this.focusFunction = STANDARD_FOCUS_FUNCTION;
        this.tagIdToFocus = tagId;
    }

    public static FocusDef focus(HtmlTag tag) {
        var result = new FocusDef(tag, STANDARD_FOCUS_FUNCTION);;
        result.focusFunction = STANDARD_FOCUS_FUNCTION;
        return result;
    }

}
