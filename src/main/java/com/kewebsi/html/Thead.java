package com.kewebsi.html;

public class Thead extends SimpleTag {

    public static final String myTag = "thead";

    public Thead() {
        super(myTag);
    }

    public Thead(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Thead(HtmlTag... children) {
        super(myTag, children);
    }

    public Thead(String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }
}
