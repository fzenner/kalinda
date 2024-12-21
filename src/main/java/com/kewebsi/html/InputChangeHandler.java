package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgFieldDataEntered;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public interface InputChangeHandler<T> {

    abstract public MsgAjaxResponse inputChanged(MsgFieldDataEntered newInput, JsonNode rootNode , UserSession userSession);

}
