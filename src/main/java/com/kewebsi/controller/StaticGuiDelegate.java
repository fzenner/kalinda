package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.HtmlPage;

/**
 * Wraps a server-side method (GuiCallbackFunction) in an object, so that it
 * can be passed around and registered.
 * Additionally, the function is linked to a unique (not enforced) string, 
 * the guiFunctionId.
 * @author A4328059
 *
 */
public class StaticGuiDelegate implements GuiDelegate  {

	protected StaticGuiCallbackFunction callback;

	/**
	 * The client will have to pass this ID to the server in order to invoke this callback function
	 */
	protected String callbackId;


	protected GuiCallbackRegistrarIntf registrar; // Not strictly needed, but for debugging nice to have.
	
	
	/**
	 * Constructror
	 * @param callback  The lambda to be invoked.
	 * @param callbackId The ID to identify this callback. It can be any string but should be unique to this scope.
	 *        The only reason to chose human readable strings here (instead of auto-generating such an id) is 
	 *        to for easier debugging and logging.
	 * @param registrar The registration object that will store this delegate.
	 */
	public StaticGuiDelegate(StaticGuiCallbackFunction callback, String callbackId, GuiCallbackRegistrarIntf registrar) {
		this.callback = callback;
		this.callbackId = callbackId;
		if (registrar != null) {
			registrar.registerGuiCallback(callbackId, this);
			this.registrar = registrar;
		} else {
			throw new CodingErrorException("Trying to register a callback '" + callbackId + "' but registrar is null");
		}
	}
	
	@Override
	public MsgAjaxResponse handleGuiMsg(JsonNode rootNode , UserSession userSession, HtmlPage htmlPage) {
		return callback.handleGuiMsg(rootNode, userSession, htmlPage);
	}
	
	// @Override
	public String getCallbackId() {
		return callbackId;
	}
}
