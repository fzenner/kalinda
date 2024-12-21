package com.fzenner.datademo.web;

import com.kewebsi.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.controller.DataDemoController;
import com.fzenner.datademo.web.inmsg.InMessageNames;
import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.fzenner.datademo.web.outmsg.MsgErrorInfo;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.ErrorOrValue;
import com.kewebsi.util.JsonUtils;
import com.kewebsi.util.SpringUtils;
/**
 * The dispatcher sits between the Ajax Servlet and the controller. It inspects the message in form of 
 * JSON nodes and assigns the correct controller to handle the message.
 * It also performs some preliminary checks on the message: It maps where possible the message to
 * the a java POJO and handles errors if necessary.  
 *
 */

public class AjaxDispatcher {

	static final Logger LOG = LoggerFactory.getLogger(AjaxDispatcher.class);
	
	
	public static final String ATTR_SERVICE_NAME = "serviceName"; // Name of JSON attribute
	public static final String ATTR_SENDING_PAGE_NAME = "pageName"; // Name of JSON attribute
	public static final String HEADER_EDIT_PAGE_ID = "EDIT_PAGE_ID"; // Name of JSON attribute
	public  final static String NO_EDIT_PAGE_ID_PRESENT = "NO_EDIT_PAGE_ID_PRESENT";
	public static final String ATTR_SENDING_BUTTON_ID = "buttonId";
	
	static int commandCounter = 0;
	
	
	// protected static GuiCallbackRegistrar guiCallbackRegistrar;
	
	
	public static MsgAjaxResponse executeServletCommand(JsonNode rootNode, UserSession userSession, HtmlPage page) {

		String msgName = extractMsgName(rootNode);
		if (!CommonUtils.hasInfo(msgName)) {
			LOG.error("Could not retrieve command name from message:" + rootNode.toPrettyString());
			return MsgAjaxResponse.createErrorMsg("Illegal command");
		}

		LOG.info("Servlet command " + msgName + " received. Command count: " + commandCounter++);
		// LOG.info("Command received:\n" + cmdName);
		
		
		InMessageNames msgTag;
		try {
			msgTag = InMessageNames.valueOf(msgName);
		} catch( Exception e) {
			LOG.error(String.format("Coud not map the cmd '%s' to a registered command", msgName));
			LOG.error(SpringUtils.exceptionToString(e));
			return MsgAjaxResponse.createErrorMsg("Unknow message received: " + msgName);
		}
		
		

		MsgAjaxResponse response = null;

		switch (msgTag) {

		case CS_MESSAGE:
			response = handleMessage(rootNode, userSession, page);
			break;

		default:
			response = MsgAjaxResponse.createErrorMsg("Unknown message received: " + msgName);
			break;
		}

		LOG.info("Servlet command #" + commandCounter + " reply: " + response.getMsgName());

		if (response.getErrorInfo() != null ) {
			LOG.info("Error occurred:" + response.getErrorInfo());
		} else {
			LOG.info("Response: " + response);
		}
		return response;
	}


	private static MsgAjaxResponse handleMessage(JsonNode rootNode, UserSession userSession, HtmlPage htmlPage) {
		String msgHandlerName = rootNode.get(ServerMessageHandler.ATTR_MESSAGE_HANDLER).asText();
		if (!CommonUtils.hasInfo(msgHandlerName)) {
			LOG.error("Could not retrieve the serverMsgHandler from message:" + rootNode);
			return MsgAjaxResponse.createErrorMsg("Illegal command");
		}
		
		
		var callbackFunction = DataDemoController.getGuiCallbackRegistrar().getCallback(msgHandlerName);
		if (callbackFunction == null) {
			LOG.error("Could not find a registered callback for serverMsgHander " + msgHandlerName);
			return MsgAjaxResponse.createErrorMsg("Command not registered: " + msgHandlerName);
		}

		MsgAjaxResponse response = callbackFunction.handleGuiMsg(rootNode, userSession, htmlPage);
		return response;

	}
	
	public static <M> ErrorOrValue<MsgErrorInfo, M> parseMsgLogError(JsonNode rootNode, Class<M> msgClass ) {
		var errorOrValue = JsonUtils.parseMsg(rootNode, msgClass);
		if (errorOrValue.hasError()) {
			LOG.error(errorOrValue.getError().toString());
		}
		return errorOrValue;
	}

	
	public static String extractMsgName(JsonNode jsonNode) {
		JsonNode messageNode = jsonNode.get(ServerMessageHandler.ATTR_MESSAGE_NAME);
		if (messageNode == null) {
			return null;
		}
		String messageName = messageNode.asText();
		return messageName;
	}
	
	
	public static String extractPageName(JsonNode jsonNode) {
		JsonNode messageNode = jsonNode.get(ATTR_SENDING_PAGE_NAME);
		if (messageNode == null) {
			return null;
		}
		String messageName = messageNode.asText();
		return messageName;
	}
	
	
	public static String extractButtonId(JsonNode jsonNode) {
		JsonNode messageNode = jsonNode.get(ATTR_SENDING_BUTTON_ID);
		if (messageNode == null) {
			return null;
		}
		String messageName = messageNode.asText();
		return messageName;
	}
	
	
	/**
	 * Extracts the service tag out of a given JSON.
	 * @return The value of the attribute <code>msgName</code>
	 */
	public static String extractMsgHandlerName(JsonNode jsonNode) {
		String serviceName = jsonNode.get(ServerMessageHandler.ATTR_MESSAGE_HANDLER).asText();
		return serviceName;
	}
	

	
	
}
