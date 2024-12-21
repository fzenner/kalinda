package com.kewebsi.html;

public class TagFactory {
    public static SimpleTag td() {
        return new SimpleTag("td");
    }

    public static SimpleTag td(String [] attrKeyValue) {
        return new SimpleTag("td", attrKeyValue);
    }




}
