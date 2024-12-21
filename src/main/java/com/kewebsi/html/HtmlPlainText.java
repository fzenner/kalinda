package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.util.JsonUtils;

public class HtmlPlainText extends HtmlTag {

    protected String htmlText;

    public HtmlPlainText(String htmlText) {
        this.htmlText = htmlText;
    }

     @Override
    public GuiDef getGuiDef() {
        return new GuiDef("plainText", null);
    }

    @Override
    public boolean isContentOrGuiDefModified() {
        return false;
    }

    @Override
    public void setContentOrGuiDefNotModified() {

    }
}
