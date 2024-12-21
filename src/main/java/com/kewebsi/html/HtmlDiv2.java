package com.kewebsi.html;


public class HtmlDiv2 extends SmartTag {

    public HtmlDiv2() {

    }


    public HtmlDiv2(String id) {
        setId(id);

    }


    public HtmlDiv2(String id, String cssClass) {
        setId(id);
        addCssClass(cssClass);
    }


    @Override
    public String getTag() {
        return "div";
    }

    @Override
    public boolean hasClosingTag() {
        return true;
    }

}