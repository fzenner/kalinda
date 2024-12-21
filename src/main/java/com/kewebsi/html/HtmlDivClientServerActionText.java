package com.kewebsi.html;

import com.kewebsi.controller.GuiDelegate;


public class HtmlDivClientServerActionText extends HtmlTagClientServerActionText {

//    public HtmlTagClientServerActionText setContentAsString(String contentAsString) {
//        this.contentAsString = contentAsString;
//        return this;
//    }

    public HtmlDivClientServerActionText() {

    }

    public HtmlDivClientServerActionText(String id, String label, String clientEventHandler, GuiDelegate serverCallback, String... constParamsKeyValue) {
        initTag("div", true, id, clientEventHandler, serverCallback, constParamsKeyValue);
        // addChild(new HtmlPlainText(label));
        setText(label);
    }



}
