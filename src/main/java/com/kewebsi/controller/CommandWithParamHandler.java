package com.kewebsi.controller;

import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

public interface CommandWithParamHandler {

    public MsgAjaxResponse handleCommandWithParam(String command, String param, UserSession userSession);

}
