package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.HtmlPage;
import org.springframework.stereotype.Service;

@Service
public class PageController {

    public static final StaticGuiDelegate loadDynamicContent  = new StaticGuiDelegate(PageController::loadDynamicContent, "PAGE_LOADER", GuiCallbackRegistrar.getInstance());


    public static MsgAjaxResponse loadDynamicContent(JsonNode rootNode, UserSession userSession, HtmlPage page) {
        page.getBody().forceReload();
        return MsgAjaxResponse.createSuccessMsg();
    }

    public static void forceRegisterStaticCallbacks() {
        if (loadDynamicContent == null) {
            throw new CodingErrorException("Problem initialing static controllers.");
        }
    }



}
