package com.kewebsi.html;

public class HtmlSpan extends SmartTag {


    public HtmlSpan(HtmlTag... children) {
        this.addChildren(children);
    }

    public HtmlSpan(HtmlTag child, String... attrKeyValues) {
        this.addChild(child);
        this.addAttributes(attrKeyValues);
    }



    @Override
    public String getTag() {
        return "span";
    }
}
