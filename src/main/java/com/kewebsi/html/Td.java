package com.kewebsi.html;

public class Td extends SimpleTag {

    public static final String myTag = "td";

    public Td() {
        super(myTag);
    }

    public Td(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Td(HtmlTag... children) {
        super(myTag, children);
    }

    public Td(String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }
}
