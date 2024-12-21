package com.kewebsi.html;

public class Div extends SimpleTag {

    public static final String myTag = "div";

    public Div() {
        super(myTag);
    }

    public Div(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Div(HtmlTag... children) {
        super(myTag, children);
    }

    public Div(String tagname, String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }
}
