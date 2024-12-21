package com.kewebsi.controller;

import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;

@FunctionalInterface
public interface GuiAction {
    MsgAjaxResponse execute();
}