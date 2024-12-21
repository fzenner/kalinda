package com.kewebsi.html;

public class Table extends SimpleTag {

    public static final String myTag = "table";

    public Table() {
        super(myTag);
    }

    public Table(String[] attrKeyValue) {
        super(myTag, attrKeyValue);
    }


    public Table(HtmlTag... children) {
        super(myTag, children);
    }

    public Table(String[] attrsKeyValue, HtmlTag... children) {
        super(myTag, attrsKeyValue, children);
    }

}
