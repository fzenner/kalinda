package com.kewebsi.html;

import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.kewebsi.controller.GuiDelegate;

public class HtmlButtonInvokeServiceWithInputFieldData extends HtmlButtonClientServerAction {

	/**
	 * 
	 * @param id
	 * @param label
	 * @param htmlDto A DTO that will be sent as JsonObject to the client. 
	 * It depicts the HTML input fields that the client is supposed to send when the button is pressed.
	 */
	// TODO: Consider slimmed implementation / test.
	public HtmlButtonInvokeServiceWithInputFieldData(String id, String label, GuiDelegate serverEventHandler, String jsonFieldIDs) {
		addChild(new HtmlPlainText(label));
		initTag("button", true, id, "CEH_genericDataSubmit", serverEventHandler, ServerMessageHandler.HTML_FIELDS_TO_SEND_DTO_ATTR_NAME, jsonFieldIDs);
	}

}

