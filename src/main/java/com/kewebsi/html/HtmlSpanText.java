package com.kewebsi.html;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.util.CommonUtils;

public class HtmlSpanText extends SmartTag {

    public HtmlSpanText() {

    }

    public HtmlSpanText(String text) {
        this.text = text;

    }


    public HtmlSpanText(String id, String text) {
        setId(id);
        this.text = text;

    }

    @Override
    public String getTag() {
        return "SPAN";
    }

    @Override
    public boolean hasClosingTag() {
        return true;
    }




}