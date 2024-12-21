package com.kewebsi.html;

public class Tbody extends SimpleTag {

    public static final String myTag = "tbody";

    public Tbody() {
        super(myTag);
    }

    public Tbody(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Tbody(HtmlTag... children) {
        super(myTag, children);
    }

    public Tbody(String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }
}
