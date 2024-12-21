package com.fzenner.datademo.web.outmsg;

public class EventHandlerDef {




    /**
     * Predefined combination of event, clientEventHandler and serverEventHandler so that the do not need tp be specified.
     */
    public String standard;

    public String event;
    public String clientEventHandler;
    public String serverEventHandler;


    public EventHandlerDef(String event, String clientEventHandler, String serverEventHandler) {
        this.event = event;
        this.clientEventHandler = clientEventHandler;
        this.serverEventHandler = serverEventHandler;
    }


    public EventHandlerDef(String standard) {
        this.standard = standard;
    }

    public static EventHandlerDef createStandardEventHandler() {
        EventHandlerDef eventHandlerDef = new EventHandlerDef("standard");
        return eventHandlerDef;
    }

}
