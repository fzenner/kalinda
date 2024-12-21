package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;


public interface GuiObjChangeHandler {

    abstract public MsgAjaxResponse inputChanged(JsonNode rootNode , UserSession userSession);

}
