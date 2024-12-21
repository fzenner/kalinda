package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public class HtmlRadioButton3 extends HtmlTag implements RadioButtonClickHandler {
    @Override
    public boolean isContentOrGuiDefModified() {
        return false;
    }

    @Override
    public void setContentOrGuiDefNotModified() {

    }

    @Override
    public MsgAjaxResponse handleClick(boolean checked, JsonNode rootNode, UserSession userSession) {
        return null;
    }
}
