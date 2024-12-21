package com.fzenner.datademo.web.inmsg;

import com.fasterxml.jackson.databind.JsonNode;

public class MsgInvokServiceWithParams extends MsgClientAction {

	
	/**
	 * JSON Attribut providing for that HTML input field data to send to the server.
	 */
	public static final String ATTR_HTML_FIELD_DATA = "htmlFieldData";
	
	/**
	 * JSON Attribut providing the constant parameters to send to the server.
	 */
	public static final String ATTR_FIXED_PARAMS = "fixedParams";
	
	/**
	 * JSON Attribut providing the name of the server-side message handler
	 */
	public static final String ATTR_SPECIAL_DATA = "specialData";
	
	
	public static JsonNode getHtmlFieldDataNode(JsonNode rootNode) {
		return rootNode.get(ATTR_HTML_FIELD_DATA);  
	}
	
	public static JsonNode getFixedParamsNode(JsonNode rootNode) {
		return rootNode.get(ATTR_FIXED_PARAMS);  
	}
	
	public static String getFixedParamAsText(JsonNode rootNode, String paramName) {
		var fixedParamsNode = getFixedParamsNode(rootNode);
		var paramNode = fixedParamsNode.get(paramName);
		return paramNode.asText();
	}
	
	
	public static JsonNode getSpecialDataNode(JsonNode rootNode) {
		return rootNode.get(ATTR_SPECIAL_DATA);  
	}
	
}
