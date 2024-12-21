package com.fzenner.datademo.web.inmsg;


import com.fzenner.datademo.web.outmsg.DateTimeWireOrNull;

public class MsgDateTimeEntered  extends MsgClientAction {
    public String tagId;
    public String clientInputState;   // EMPTY, INCOMPLETE, UNPARSEABLE, OK
    public DateTimeWireOrNull value;


    public enum InputElementStateInfoFromClientToServer {CLIENT_INPUT_EMPTY, CLIENT_INPUT_NOT_TRANSMISSABLE, CLIENT_INPUT_FILLED_OK};

    // public enum InputElementStateInfoFromClientToServer{CLIENT_INPUT_EMPTY, CLIENT_INPUT_INCOMPLETE, CLIENT_INPUT_UNPARSEABLE, CLIENT_INPUT_FILLED_OK}

}