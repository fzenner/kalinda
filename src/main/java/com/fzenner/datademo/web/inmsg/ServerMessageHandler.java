package com.fzenner.datademo.web.inmsg;

public class ServerMessageHandler {
	
	/**
	 * Indicates a functional (not technical) client-server message.
	 */
	// public static final String CS_MESSAGE = "CS_MESSAGE";  
	// public static final String SERVER_MSG_HANDLER_GENERIC_DATA_SUBMIT = "SERVER_MSG_HANDLER_GENERIC_DATA_SUBMIT";
	// public static final String SERVER_MSG_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT = "SERVER_MSG_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT";
	// public static final String SERVER_MSG_HANDLER_SELECT_TABLE_ROWS = "SERVER_MSG_HANDLER_SELECT_TABLE_ROWS";
	
	
	
	// public static final String SERVER_EVENT_HANDLER_ATTR_NAME = "data-servereventhandler";  // deprictated: TODO: Remove
	public static final String SERVER_MSG_HANDLER_ATTR_NAME = "data-servermsghandler";
	public static final String SERVER_COMMAND_ATTR_NAME = "data-servercmd";
	public static final String SERVER_SUB_COMMAND_ATTR_NAME = "data-serversubcmd";
	public static final String SERVER_SIDE_ID_ATTR_NAME = "data-serversideid";
	
	/**
	 * Prefix of the attribute that is used to pass additional server message handlers to the HTML tag.
	 * It should be postfixed with numbers, starting with 1, e.g. data-servermsghandler-1
	 */
	public static final String SERVER_MSG_HANDLER_MODAL_CLOSE = "data-servermsghandlermodalclose";  
	
	/**
	 * Name of the HTML attribute that stores the dto of the HTML fields that should be sent
	 * with the corresponding message.
	 */
	public static final String HTML_FIELDS_TO_SEND_DTO_ATTR_NAME = "data-htmlfieldstosenddto";
	
	/**
	 * Name of the HTML attribute that stores the dto of the fixed parameters that should be sent
	 * with the corresponding message.
	 */
	public static final String FIXED_PARAMS_TO_SEND_DTO_ATTR_NAME = "data-fixedparamstosenddto";
	
	
	public static final String SPECIAL_DATA_TO_SEND_DTO_ATTR_NAME = "data-specialdatatosenddto";
	
	/**
	 * JSON Attribute providing the name of an incoming message. For most messages this is CS_MESSAGE.
	 */
	public static final String ATTR_MESSAGE_NAME = "msgName";              // Name of JSON attribute 
	
	/**
	 * JSON Attribut providing the name of the server-side message handler
	 */
	public static final String ATTR_MESSAGE_HANDLER = "serverMsgHandler";     // Name of JSON attribute
	

	
	
	
	
}
