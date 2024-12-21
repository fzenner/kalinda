package com.kewebsi.html;


import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.inmsg.MsgInvokeServiceWithSimpleParams;
import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.kewebsi.controller.EscapeKeyHandler;
import com.kewebsi.controller.GuiDelegate;
import com.kewebsi.controller.StandardController;

public abstract class HtmlTagClientServerAction extends SmartTag  {
	protected String tagName;
	protected boolean hasClosingTag;




	public HtmlTagClientServerAction() {
		
	}


	@Override
	public String getTag() {
		return tagName;
	}


	public HtmlTagClientServerAction initTag(String tagName, boolean hasClosingTag, String id, String clientEventHandler, String... specialAttribues) {
		setId(id);
		this.tagName = tagName;
		this.hasClosingTag = hasClosingTag;
		putNonStandardAttribute(ClientEventHandlerConsts.EVENT_TO_HANDLE_ATTR_NAME_1,      "click");
		putNonStandardAttribute(ClientEventHandlerConsts.CLIENT_EVENT_HANDLER_ATTR_NAME_1, clientEventHandler);

		if (specialAttribues != null) {
			nonStandardAttributes.put(specialAttribues);
		}

		return this;
	}

	public HtmlTagClientServerAction initTag(String tagName, boolean hasClosingTag, String id, String clientEventHandler, GuiDelegate serverMsgHandler,
			String... specialAttribues) {
		setId(id);
		this.tagName = tagName;
		this.hasClosingTag = hasClosingTag;
		// this.serverCallBackId = serverMsgHandler.getCallbackId();
		putNonStandardAttribute(ClientEventHandlerConsts.EVENT_TO_HANDLE_ATTR_NAME_1,      "click");
		putNonStandardAttribute(ClientEventHandlerConsts.CLIENT_EVENT_HANDLER_ATTR_NAME_1, clientEventHandler);

		if (serverMsgHandler != null) {
			putNonStandardAttribute(ServerMessageHandler.SERVER_MSG_HANDLER_ATTR_NAME,       serverMsgHandler.getCallbackId());
		}

		if (specialAttribues != null) {
			nonStandardAttributes.put(specialAttribues);
		}

		return this;
	}


	

	public HtmlTagClientServerAction initTag(String tagName, boolean hasClosingTag, String id, String eventName, String clientEventHandler, GuiDelegate serverMsgHandler,
											 String... specialAttribues) {
		setId(id);
		this.tagName = tagName;
		this.hasClosingTag = hasClosingTag;

		setClientEventHandler(eventName, clientEventHandler);

		if (serverMsgHandler != null) {
			setServerMsgHandler(serverMsgHandler);
		}

		nonStandardAttributes.put(specialAttribues);

		return this;
	}


	public HtmlTagClientServerAction setTagNameAndId(String tagName, String id) {
		this.tagName = tagName;
		this.id = id;
		return this;
	}


	
	public HtmlTagClientServerAction setClientEventHandler(String eventName, String clientEventHandler) {
		putNonStandardAttribute(ClientEventHandlerConsts.EVENT_TO_HANDLE_ATTR_NAME_1,      eventName);
		putNonStandardAttribute(ClientEventHandlerConsts.CLIENT_EVENT_HANDLER_ATTR_NAME_1, clientEventHandler);
		addEventHandlerClass();
		return this;
	}
	
	public HtmlTagClientServerAction setServerMsgHandler(GuiDelegate serverMsgHandler) {
		putNonStandardAttribute(ServerMessageHandler.SERVER_MSG_HANDLER_ATTR_NAME,       serverMsgHandler.getCallbackId());
		return this;
	}

	public HtmlTagClientServerAction setEventHandlers(String eventName, String clientEventHandler, GuiDelegate serverMsgHandler) {
		putNonStandardAttribute(ClientEventHandlerConsts.EVENT_TO_HANDLE_ATTR_NAME_1,      eventName);
		putNonStandardAttribute(ClientEventHandlerConsts.CLIENT_EVENT_HANDLER_ATTR_NAME_1, clientEventHandler);
		putNonStandardAttribute(ServerMessageHandler.SERVER_MSG_HANDLER_ATTR_NAME,       serverMsgHandler.getCallbackId());
		addEventHandlerClass();
		return this;
	}

	/**
	 *
	 * @param idOfTagToClose Allows to handle the event in one tag and close another one
	 *                       (which we might not add event handling to).
	 */
	public HtmlTagClientServerAction addEscapeKeyHandling(String idOfTagToClose) {
		//
		// Handling for escape key event
		//
		setClientEventHandler("keydown", "CEH_escapeKeyPressed");
		setServerMsgHandler(StandardController.SMH_escapeKeyPressed);
		putNonStandardAttribute(MsgInvokeServiceWithSimpleParams.ATTR_P1, idOfTagToClose);
		return this;

	}

	public HtmlTagClientServerAction addEscapeKeyHandling() {
		return addEscapeKeyHandling(id);
	}


	@Override
	public boolean hasClosingTag() {
		return hasClosingTag;
	}



}
