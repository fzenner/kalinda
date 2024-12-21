package com.kewebsi.html;

public class Tr extends SimpleTag {

    public static final String myTag = "tr";

    public Tr() {
        super(myTag);
    }

    public Tr(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Tr(HtmlTag... children) {
        super(myTag, children);
    }

    public Tr(String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }
}
