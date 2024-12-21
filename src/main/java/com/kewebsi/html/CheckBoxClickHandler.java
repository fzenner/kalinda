package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public interface CheckBoxClickHandler {

    abstract public MsgAjaxResponse handleClick(boolean checked, JsonNode rootNode , UserSession userSession);

}
