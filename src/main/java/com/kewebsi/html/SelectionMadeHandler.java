package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public interface SelectionMadeHandler {

    abstract public MsgAjaxResponse handleSelectionEvent(String newValue, JsonNode rootNode , UserSession userSession);

}
