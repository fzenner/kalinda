package com.kewebsi.controller;

import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

@FunctionalInterface
public interface EscapeKeyHandler {
    MsgAjaxResponse handleEscapeKeyPressed(UserSession userSession);

}