package com.kewebsi.html.popup;

import com.kewebsi.controller.GuiDelegate;
import com.kewebsi.html.HtmlTag;

public class ModalDialogDefOld {

    public String id;
    public String modalText;
    public String confirmButtonStr;
    public String cancelButtonStr;

    public String serverMsgHandler;

    /**
     * The server-side HTML HtmlTag to be invoked by the callback function.
     */
    public String serverHtmlTargetId;

    /**
     * A parameter to be passed to the handler method of the target.
     */
    public String serverCallbackCommand;

    public String serverCallbackParam;


    public ModalDialogDefOld(String id, String modalText, String confirmButtonStr, String cancelButtonStr, GuiDelegate serverMsgHandler, HtmlTag serverHtmlTarget, String serverCallbackCommand, String serverCallbackParam) {
        this.id = id;
        this.modalText = modalText;
        this.confirmButtonStr = confirmButtonStr;
        this.cancelButtonStr = cancelButtonStr;
        this.serverMsgHandler = serverMsgHandler.getCallbackId();
        this.serverHtmlTargetId = serverHtmlTarget.getId();
        this.serverCallbackCommand = serverCallbackCommand;
        this.serverCallbackParam = serverCallbackParam;
    }


    public ModalDialogDefOld(String id, String modalText, String confirmButtonStr, String cancelButtonStr, GuiDelegate serverMsgHandler, HtmlTag serverHtmlTarget, String serverCallbackCommand) {
        this.id = id;
        this.modalText = modalText;
        this.confirmButtonStr = confirmButtonStr;
        this.cancelButtonStr = cancelButtonStr;
        this.serverMsgHandler = serverMsgHandler.getCallbackId();
        this.serverHtmlTargetId = serverHtmlTarget.getId();
        this.serverCallbackCommand = serverCallbackCommand;
        this.serverCallbackParam = null;
    }
}
