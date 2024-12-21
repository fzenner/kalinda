package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public interface CustomGuiEventHandler {
    abstract public MsgAjaxResponse handleCustomGuiEvent(JsonNode rootNode , UserSession userSession);
}


