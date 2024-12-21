package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.HtmlPage;


public class DynamicGuiDelegate<T> implements GuiDelegate {

	protected T objectToInvoke;
	protected DynamicGuiCallbackFunction<T> callback;

	/**
	 * The client will have to pass this ID to the server in order to invoke this callback function
	 */
	protected String callbackId;


	protected GuiCallbackRegistrarIntf registrar; // Not strictly needed, but for debugging nice to have.
	
	
	public DynamicGuiDelegate(DynamicGuiCallbackFunction<T> callback) {
		
	}
	
	
	
	/**
	 * Constructror
	 * @param callback  The lambda to be invoked.
	 * @param callbackId The ID to identify this callback. It can be any string but should be unique to this scope.
	 *        The only reason to chose human readable strings here (instead of auto-generating such an id) is 
	 *        to for easier debugging and logging.
	 * @param registrar The registration object that will store this delegate.
	 */
	public DynamicGuiDelegate(T objectToInvoke, DynamicGuiCallbackFunction<T> callback, String callbackId, GuiCallbackRegistrarIntf registrar) {
		this.objectToInvoke = objectToInvoke;
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
	public MsgAjaxResponse handleGuiMsg(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		return callback.callbackLambda(objectToInvoke, rootNode, userSession, page);
	}
	
	@Override
	public String getCallbackId() {
		return callbackId;
	}
}
