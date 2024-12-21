package com.kewebsi.html;

public class Th extends SimpleTag {

    public static final String myTag = "th";

    public Th() {
        super(myTag);
    }

    public Th(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Th(HtmlTag... children) {
        super(myTag, children);
    }

    public Th(String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }
}
