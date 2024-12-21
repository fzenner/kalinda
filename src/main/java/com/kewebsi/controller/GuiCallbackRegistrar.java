package com.kewebsi.controller;

import java.util.HashMap;

import com.kewebsi.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.CodecException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.kewebsi.errorhandling.ErrorInClientServerLinkingException;
import com.kewebsi.util.CommonUtils;

public class GuiCallbackRegistrar implements GuiCallbackRegistrarIntf {

	
	public static GuiCallbackRegistrar globalInstance;  // Might be replaced by one instance per module in the future,

	protected boolean controllersInitialized = false;
	
	private static Logger LOG = LoggerFactory.getLogger(GuiCallbackRegistrar.class);
	
	protected HashMap<String, GuiDelegate> functionMap = new HashMap<>();
	
	
	public void registerGuiCallback(String callbackId, GuiDelegate funToRegister) {
		
		if (functionMap.containsKey(callbackId)) {
			throw new CodecException("Double registration of callbackId: " + callbackId);
		}
		functionMap.put(callbackId, funToRegister);
		
		LOG.info("Registered " + callbackId + " on " + funToRegister.toString());
	}
	
	
	public void invokeGuiCallback(String callbackId, JsonNode rootNode , UserSession userSession, HtmlPage htmlPage ) {
		GuiDelegate callback = functionMap.get(callbackId);
		
		if (callback == null) {
			throw new ErrorInClientServerLinkingException("No callback found for: " + callbackId);
		}
		
		callback.handleGuiMsg(rootNode, userSession, htmlPage);
		
	}
	
	public GuiDelegate getCallback(String callbackId) {
		return functionMap.get(callbackId);
	}
	
	
	/**
	 * Get the id for HTML linking purposes.
	 * @param callbackFunction The function that should be linked with the returned id.
	 * @return The id of the given callback function, if it was registered before.
	 * 
	 * Throws exception if the callbackFunction was not registered.
	 */
	public String getIdForCallback(GuiDelegate callbackFunction) {
		// registerGuiCallbacks();
		String id = CommonUtils.getKey(functionMap, callbackFunction);
		if (id == null) {
			throw new ErrorInClientServerLinkingException("No function registered for lambda: " + callbackFunction.toString());
		}
		
		return id;
	}
	
	
	public static GuiCallbackRegistrar getInstance() {
		if (globalInstance == null) {
			globalInstance = new GuiCallbackRegistrar();
		}

		return globalInstance;
	}

}
