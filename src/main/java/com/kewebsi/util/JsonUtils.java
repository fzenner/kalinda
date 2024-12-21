package com.kewebsi.util;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.html.HtmlTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
// import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fzenner.datademo.web.outmsg.MsgErrorInfo;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.InputParsingException;

public class JsonUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

//	public static <T> T parseCmd(String cmdJson, Class<T> cls) {
//		T result = null;
//		try {
//			result = new Gson().fromJson(cmdJson, cls);
//		}
//		catch(Exception e) {
//			LOG.error("Exception caught during parsing of json. JSON string is: \n" + cmdJson, e );
//		}
//		return result;
//	}
	
	
	public static <T> T parseJson(String cmdJson, Class<T> cls) {
		T result = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			result = objectMapper.readValue(cmdJson, cls);
		}
		catch(Exception e) {
			LOG.error("Exception caught during parsing of json. JSON string is: \n" + cmdJson, e );
		}
		return result;
	}
	
	public static JsonNode parseJsonToNode(String cmdJson) {
		JsonNode result = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			result = objectMapper.readTree(cmdJson);
		}
		catch(Exception e) {
			LOG.error("Exception caught during parsing of json. JSON string is: \n" + cmdJson, e );
		}
		return result;
	}
	
	public static <T> T parseJson(JsonNode cmdJson, Class<T> cls) throws JsonProcessingException {
		T result = null;
		// try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			result = objectMapper.treeToValue(cmdJson, cls);
//		}
//		catch(Exception e) {
//			LOG.error("Exception caught during parsing of json. JSON string is: \n" + cmdJson, e );
//		}
		return result;
	}
	
	/**
	 * Standard JSON parsing, except that it throws a MalformedClientDataException in case of an error.
	 * @param <T>
	 * @param cmdJson
	 * @param cls
	 * @return
	 */
	public static <T> T parseJsonFromClient(JsonNode cmdJson, Class<T> cls)  {
		T result = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			result = objectMapper.treeToValue(cmdJson, cls);
		} catch(Exception e) {
			throw new MalformedClientDataException(e);
		}
		return result;
	}
	
	public static String parseChildNode(JsonNode parentNode, String childNodeKey) throws JsonProcessingException {
		JsonNode childNode = parentNode.get(childNodeKey);
		if (childNode == null) {
			throw new InputParsingException("No node with name " + childNodeKey + " found.");
		}
		String result = parseJson(childNode, String.class);
		return result;
	}
	
	
	public static int parseChildNodeInt(JsonNode parentNode, String childNodeKey) throws JsonProcessingException {
		String s = parseChildNode(parentNode, childNodeKey);
		return Integer.valueOf(s);
	}
	
	
	
	public static String getValueOfChildNode(JsonNode parent, Enum<?> childFieldName) {
		JsonNode valueNode = parent.get(childFieldName.name());
		String result = valueNode.asText();
		return result;
	}
	
	
	
	
	

	public static String generateJson(Object obj)  {
		String result = null;
		ObjectMapper objectMapper = new ObjectMapper();
		// objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		 objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		try {
			result = objectMapper.writeValueAsString(obj);
		} catch(JsonProcessingException e) {
			throw new CodingErrorException(e);
		}
		return result;
	}

	public static JsonNode generateJsonTree(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			var rootNode = objectMapper.valueToTree(obj);
			return rootNode;
		} catch (Exception e) {
			System.out.println("Exception!!!!!!!!");
		}
		return null;
	}


	public static int parseIntDefault(String toParse, int defaultInCaseOfError) {
		int result = defaultInCaseOfError;
		try {
			result = Integer.parseInt(toParse);
		} catch (Exception e) {
			LOG.warn("Integer could not be parsed: " + toParse);
		}
		return result;
	}

	public static <M> ErrorOrValue<MsgErrorInfo, M> parseMsg(JsonNode rootNode, Class<M> msgClass ) {
		M msgExecuteSearch = null;
		String errorMsg = null;   // non-null, if error occurred;
		try {
			msgExecuteSearch= parseJson(rootNode,  msgClass);
		} 
		catch( JsonProcessingException e) {
			errorMsg = " Error occurred mapping JSON to " + msgClass.getSimpleName() + ". Exception: " + e.getLocalizedMessage() +"\n JSON:\n" + rootNode.toPrettyString();
		}
		
		var errorOrValue = new ErrorOrValue<MsgErrorInfo, M>();
		if (errorMsg != null) {
			errorOrValue.setError(new MsgErrorInfo(errorMsg));
		} else {
			errorOrValue.setValue(msgExecuteSearch);
		}
		return errorOrValue;
	}
	
	
	public static <M> M parseMsgNew(JsonNode rootNode, Class<M> msgClass ) throws CodingErrorException {
		try {
			return parseJson(rootNode,  msgClass);
		} 
		catch( JsonProcessingException e) {
			throw new CodingErrorException("Error occurred mapping JSON to " + msgClass.getSimpleName(), e);
		}
	}
	
	public static String createJsonStr(String... keyValue) {
		String out = "{" + createJsonKeyValue(keyValue) + "}";
		return out;
	}
	
	public static String createJsonStrDoubleQuote(String... keyValue) {
		String out = "{" + createJsonKeyValueDoubleQuote(keyValue) + "}";
		return out;
	}

	/**
	 * Creates a String of the format <code>"key1":"value1", "key2":"value2", ...</code> to be used in the creation of JSON objects.
	 * The key string as well as the value string will be enclosed in double quotes.
	 * @param keyValue Array of Strings alternating key and value, that is key1 value1 key2 value2 ...
	 * @return The formatted string
	 */
	public static String createJsonStrDoubleQuote(ArrayList<String> keyValue) {
		String[] arr = keyValue.toArray(keyValue.toArray(new String[0]));
		String out = "{" + createJsonKeyValueDoubleQuote(arr) + "}";
		return out;
	}
	
	
	public static String createJsonStr(String key, Enum<?> value) {
		return createJsonStr(key, value.name());
	}
	
	public static String createJsonStr(String key1, Enum<?> value1, String key2, Enum<?> value2) {
		return createJsonStr(key1, value1.name(), key2, value2.name());
	}
	
	
	/**
	 * Generates a string of the format (without the outer single quotes) as shown by
	 * the following example:
	 * '["enum1Value1", "enumValue2", "enumValue3"]'
	 * which is a correct JSON object (array).
	 * @param enums
	 * @return
	 */
	public static String generateJsonArray(Enum<?>[] enums ) {
		String out = "[";
		int count = enums.length;
		int lastIdxWithComma = count-2;
		
		for (int i=0; i<count; i++) {
			var enumRun = enums[i];
			out += "\"" + enumRun.name() + "\"";
			if (i <= lastIdxWithComma) {
				out += ", ";
			}
		}
		out += "]";
		return out;
	}


	public static String generateJsonArray(EnumSet<?> enums ) {
		Enum<?>[] enumArray0 = new Enum<?>[enums.size()];
		var enumArray =   enums.toArray(enumArray0);
		return generateJsonArray(enumArray);
	}



	public static String generateJsonArrayV2(EnumSet<?> enums ) {


		String out = "[";
		int count = enums.size();
		int lastIdxWithComma = count-2;
		int i=0;
		for (Enum<?> enumRun : enums) {
			out += "\"" + enumRun.name() + "\"";
			if (i <= lastIdxWithComma) {
				out += ", ";
			}
			i++;
		}
		out += "]";
		return out;
	}

	
	
	public static String generateJsonArray(String[] arrIn ) {
		String out = "[";
		int count = arrIn.length;
		int lastIdxWithComma = count-2;
		
		for (int i=0; i<count; i++) {
			var enumRun = arrIn[i];
			out += "\"" + enumRun + "\"";
			if (i <= lastIdxWithComma) {
				out += ", ";
			}
		}
		out += "]";
		return out;
	}
	

	/**
	 * Creates a string of the format required for HTML tag attributes. 
	 * That is a space separated list off attribute value pairs with the value
	 * put in single quotes. Example (without outer quotes): 
	 * "attrName1:'value1', attr2='value2'"
	 * If the key is null, empty or consists of only blanks, the attribute-value pair is not generated.
	 * If the value is null, the attribute-value pair is also not generated. 
	 * However, if the value is an empty or only-blanks string, the attribute-value pair is generated (if the also the key is valid of course).
	 * This way, unnecessary pairs are for non-info values are not generated, unless you provide intentionally the combination of a real key and an empty string. 
	 * @param keyValue List of alternating attribute names and values
	 * @return As described above.
	 */
	public static String createJsonKeyValue(String... keyValue) {
		String out = "";
		if (keyValue != null) {
			if (keyValue.length % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<keyValue.length) {
				String key = keyValue[i++];
				String value = keyValue[i++];
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						out += key + ":'" + value + "' ";
						if (i < keyValue.length) {
							out += ", ";
						} else {
							out += " ";
						}
					}
				}
			}
		}
		return out;
	}


	/**
	 * Creates a string of the format <br><br>
	 * <code>key1='value1' key2='value2'</code><br><br>
	 * If the value is null, then only the key is generated for example <br><br>
	 * <code>key1='value1' key2 key3='value3'</code> <br><br>
	 * That variant is for example
	 * used by the attribute <code>checked<code> in HTML checkboxes
	 */
	public static String createHtmlAttributes(LinkedHashMap<String, String> keyValue) {
		String out = "";
		if (keyValue != null) {
			for (var run : keyValue.entrySet()) {
				String key = run.getKey();
				String value = run.getValue();
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						out += key + "='" + value + "' ";
					} else {
						out += key + " ";
					}
				}
			}
		}
		return out;
	}


	public static void addAttributes(LinkedHashMap<String, String> keyValue, ObjectNode toAddTo) {
		if (keyValue != null) {
			for (var run : keyValue.entrySet()) {
				String key = run.getKey();
				String value = run.getValue();
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						toAddTo.put(key, value);
					} else {
						toAddTo.putNull(key);
					}
				}
			}
		}
	}



	/**
	 * Uitilty function to generate the attributes of an HTML element.
	 *
	 * Creates a String of the form
	 * <br><code></code>key1=value1 key2='value2'</code> <br>
	 *
	 * If the value is null, then no attribute is generated.
	 *
	 * @param keyValue
	 * @return
	 */
	public static String createHtmlAttributes(String... keyValue) {
		String out = "";
		if (keyValue != null) {
			if (keyValue.length % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<keyValue.length) {
				String key = keyValue[i++];
				String value = keyValue[i++];
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						out += key + "='" + value + "' ";
					}
				}
			}
		}
		return out;
	}


	public static void addAttributes(ObjectNode attrs, String... keyValue) {
		if (keyValue != null) {
			if (keyValue.length % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<keyValue.length) {
				String key = keyValue[i++];
				String value = keyValue[i++];
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						attrs.put(key, value);
					} else {
						attrs.putNull(key);
					}
				}
			}
		}
	}

	public static void addAttributes(ObjectNode attrs, ArrayList<String> keyValue) {
		String[] strArr = new String[keyValue.size()];
		strArr = keyValue.toArray(strArr);
		addAttributes(attrs, strArr);
	}

	public static void addAttributesNoEmptyClass(ObjectNode attrs, String... keyValue) {
		if (keyValue != null) {
			if (keyValue.length % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<keyValue.length) {
				String key = keyValue[i++];
				String value = keyValue[i++];
				if (CommonUtils.hasInfo(key)) {
					if (! key.equals("class") || CommonUtils.hasInfo(value)) {  // We skip class attributes without value
						if (value != null) {
							attrs.put(key, value);
						} else {
							attrs.putNull(key);
						}
					}
				}
			}
		}
	}


	/**
	 * Creates a String of the format <code>"key1":"value1", "key2":"value2", ...</code> to be used in the creation of JSON objects.
	 * The key string as well as the value string will be enclosed in double quotes.
	 * @param keyValue Array of Strings alternating key and value, that is key1 value1 key2 value2 ...
	 * @return The formatted string
	 */
	public static String createJsonKeyValueDoubleQuote(String... keyValue) {
		String out = "";
		if (keyValue != null) {
			if (keyValue.length % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<keyValue.length) {
				String key = keyValue[i++];
				String value = keyValue[i++];
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						out += "\"" + key + "\":\"" + value + "\"";
					}
					if (i < keyValue.length) {
						out += ", ";
					} else {
						out += " ";
					}
				}
			}
		}
		return out;
	}
	
	
	public static String createJsonKeyValue(ArrayList<String> keyValue) {
		String[] strArr = new String[keyValue.size()];
		strArr = keyValue.toArray(strArr);
		return createJsonKeyValue(strArr);
	}
	
	
	public static String createHtmlAttributes(ArrayList<String> keyValue) {
		String[] strArr = new String[keyValue.size()];
		strArr = keyValue.toArray(strArr);
		return createHtmlAttributes(strArr);
	}
	
	
	
	public static String createHtmlTag(String tagType, String id, String classes, String content, String... keyValue) {
		content = content==null ? "" : content;
		String result = "<" + tagType + " id='" + id + "' class='" + classes + "' " + createHtmlAttributes(keyValue) + ">" + content + "</" + tagType + ">";  
		return result;
	}
	
	/**
	 * Creates an HTML tag without the corresponding closing tag, e.g. for <input> fields. 
	 * @param tagType
	 * @param id
	 * @param classes
	 * @param content
	 * @param keyValue
	 * @return
	 */
	public static String createHtmlTagNonClosing(String tagType, String id, String classes, String content, String... keyValue) {
		content = content==null ? "" : content;
		String result = "<" + tagType + " id='" + id + "' class='" + classes + "' " + createHtmlAttributes(keyValue) + ">" + content + "</" + tagType + ">";  
		return result;
	}
	

	public static String createHtmButton(String id, String classes, String content, String... keyValue) {
		return createHtmlTag("button", id, classes, content, keyValue);
	}

	public static ObjectNode createJsonNode(String tagName) {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(HtmlTag.TAGNAME, tagName);
		return objectNode;
	}

	public static ObjectNode createJsonNode(String tagName, String... attrKeyValue) {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(HtmlTag.TAGNAME, tagName);

		if (attrKeyValue.length > 0) {
			ObjectNode attrs = createAttributesNode(objectNode);
			addAttributes(attrs, attrKeyValue);
		}
		return objectNode;
	}

	public static ArrayNode initChildrenArrayNode(ObjectNode parentNode) {
		ArrayNode childrenArrayNode = (ArrayNode) parentNode.get(HtmlTag.CHILDREN);
		if (childrenArrayNode == null) {
			ObjectMapper objectMapper = new ObjectMapper();
			childrenArrayNode = objectMapper.createArrayNode();
			parentNode.set(HtmlTag.CHILDREN, childrenArrayNode);
		}
		return childrenArrayNode;
	}

	public static ObjectNode createChildNode(ObjectNode parentNode, String tagName, String... attrKeyValue) {
		ArrayNode childrenArrayNode = initChildrenArrayNode(parentNode);
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode childNode = objectMapper.createObjectNode();
		childrenArrayNode.add(childNode);
		childNode.put(HtmlTag.TAGNAME, tagName);
		ObjectNode attrs = HtmlTag.getAttributesNodeAdding(childNode, attrKeyValue);
		return childNode;
	}

	public static void addChildNode(ObjectNode parentNode, JsonNode childNode) {
		ArrayNode childrenArrayNode = initChildrenArrayNode(parentNode);
		childrenArrayNode.add(childNode);
	}

	public static void addChildNodes(ObjectNode parentNode, ObjectNode... childNodes) {
		ArrayNode childrenArrayNode = initChildrenArrayNode(parentNode);
		for (var childNode : childNodes) {
			childrenArrayNode.add(childNode);
		}
	}

	/**
	 * Creates an ObjectNode with the attributes
	 * tagname=TEXT_NODE and text="t"he text".
	 * Note: This is not a Jackson-TextNode but rather an ObjectNode that defines a plain text html
	 * element for the client.
	 * @param text
	 * @return
	 */
	public static ObjectNode createTextNode(String text) {
		var textNode = new ObjectMapper().createObjectNode();
		textNode.put(HtmlTag.TAGNAME, HtmlTag.TEXT_NODE);
		textNode.put("text", text);
		return textNode;
	}

	public static ObjectNode createTextChildNode(ObjectNode parentNode, String text) {
		var childNode = createChildNode(parentNode, HtmlTag.TEXT_NODE);
		childNode.put("text", text);
		return childNode;
	}

	public static ObjectNode createJsonNodeNoEmptyClass(String tagName, String... attrKeyValue) {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(HtmlTag.TAGNAME, tagName);
		if (nonEmptyClassAttributesExist(attrKeyValue)) {
			ObjectNode attrs = createAttributesNode(objectNode);
			addAttributesNoEmptyClass(attrs, attrKeyValue);
		}
		return objectNode;
	}

	private static boolean nonEmptyClassAttributesExist(String... keyValue) {
		int i = 0;
		while (i<keyValue.length) {
			String key = keyValue[i++];
			String value = keyValue[i++];
			if (CommonUtils.hasInfo(key)) {
				if (!key.equals("class")) {
					return true;
				} else {
					if (CommonUtils.hasInfo(value)) {
						return true;
					} else {
						// When here, we have found a blank class and continue to search for a relevant entry.
					}
				}
			}
		}
		return false;
	}

	public static ObjectNode createAttributesNode(ObjectNode node) {
		ObjectNode attrNode = (ObjectNode) node.get(HtmlTag.ATTRS);

		if (attrNode == null) {
			ObjectMapper objectMapper = new ObjectMapper();
			attrNode = objectMapper.createObjectNode();
			node.putIfAbsent(HtmlTag.ATTRS, attrNode);
		}
		return attrNode;
	}


	public static String jsonNodeToHtml(JsonNode jsonNode) {
		String out = "";

		String myTagName = jsonNode.get(HtmlTag.TAGNAME).textValue();
		out += "<" + myTagName;

		ObjectNode attrNode = (ObjectNode) jsonNode.get(HtmlTag.ATTRS);

		if (attrNode != null) {
			var fields = attrNode.fields();
			while (fields.hasNext()) {
				var field = fields.next();
				out += " " + field.getKey() + "='" + field.getValue().textValue() +"'";
			}
		}

		out += ">";

		ArrayNode childrenArrayNode = (ArrayNode) jsonNode.get(HtmlTag.CHILDREN);
		if (childrenArrayNode != null) {
			var childIterator = childrenArrayNode.iterator();
			while (childIterator.hasNext()) {
				var child = childIterator.next();
				out += "\n" + jsonNodeToHtml(child);
			}
		}

		if (! myTagName.equalsIgnoreCase("input")) {
			out += "\n" + "</" + myTagName + ">";
		}

		return out;

	}

	

}
