package com.fzenner.datademo.web.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.kewebsi.errorhandling.ErrorInSessionHandling;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.HtmlTag;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.AjaxDispatcher;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.UserSessionHandler;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.fzenner.datademo.web.outmsg.MsgErrorInfo;
import com.fzenner.datademo.web.outmsg.MsgClientUpdate;
import com.kewebsi.html.HtmlPage;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static com.fzenner.datademo.web.AjaxDispatcher.HEADER_EDIT_PAGE_ID;
import static com.fzenner.datademo.web.AjaxDispatcher.NO_EDIT_PAGE_ID_PRESENT;


//Extend HttpServlet class
@WebServlet(
		  name = "MessageServlet",
		  description = "Example Servlet Using Annotations",
		  urlPatterns = {"/ajaxServlet"}
		)
public class AjaxServlet extends HttpServlet {

	@Autowired
	protected UserSessionHandler userSessionHandler;

	private static final long serialVersionUID = -770426353828524364L;
	private static Logger LOG = LoggerFactory.getLogger(AjaxServlet.class);

	private String message;

	public void init() throws ServletException {
		// Do required initialization
		message = "Hello World";
	}



	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>");
	}


	public static MultiValueMap<String, String> parseRequestParams(String uri) {
		UriComponents comps = UriComponentsBuilder.fromUriString(uri).build();
		var params = comps.getQueryParams();
		return params;
	}

	public static String getPath(String uri) {
		UriComponents comps = UriComponentsBuilder.fromUriString(uri).build();
		var result = comps.getPath();
		return result;
	}


	public static Map<String, String[]> convertSpringStyleMapToStandardMap(MultiValueMap<String, String> inMap) {
		if (inMap == null) {
			return null;
		}
		int size = inMap.size();
		HashMap<String, String[]> outMap = new HashMap<>(size);

		var iterator = inMap.entrySet().iterator();
		while (iterator.hasNext()) {
			var entry = iterator.next();
			String key = entry.getKey();
			List<String> listOfVals = entry.getValue();
			String[] stringArrayOut = listOfVals.toArray(new String[size]);
			outMap.put(key, stringArrayOut);
		}
		return outMap;
	}


	public static void exampleMappingFunctionForStudy(String[] args) {
		String uri = "http://my.test.com/test?param1=ab&param2=cd&param2=ef";
		// UriComponentsBuilder UriComponentsBuilder;


		UriComponents comps = UriComponentsBuilder.fromUriString(uri).build();
		MultiValueMap<String, String> params = comps.getQueryParams();


		MultiValueMap<String, String> parameters =
				UriComponentsBuilder.fromUriString(uri).build().getQueryParams();
		List<String> param1 = parameters.get("param1");
		List<String> param2 = parameters.get("param2");
		System.out.println("param1: " + param1.get(0));
		System.out.println("param2: " + param2.get(0) + "," + param2.get(1));
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		

		UserSession userSession = userSessionHandler.getUserSession(request);

		
		BufferedReader reader = request.getReader();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		/*
		 * Get the message.
		 */
		String payload = CommonUtils.bufferedReaderToString(reader);
		LOG.debug("Ajax payload received (see next lines):\n" + payload);
		
		JsonNode rootNode = JsonUtils.parseJsonToNode(payload);
		
		if (rootNode == null) {
			logAndReturnJsonErrorMsg("Message is not in JSON format: " + payload, out);
			return;
		}

		/*
		 * Get the page. Handle errors robustly.
		 */
		HtmlPage page;
		// int editPageId = extractEditPageId(rootNode);
		int editPageId = -1;

		String editPageIdStr = request.getHeader(HEADER_EDIT_PAGE_ID);
		if (editPageIdStr.equals(NO_EDIT_PAGE_ID_PRESENT)) {
			editPageId = -1;
		} else {
			try {
				editPageId = Integer.parseInt(editPageIdStr);
			} catch (NumberFormatException nfe) {
				throw new MalformedClientDataException(nfe.getMessage());
			}

		}



		if (editPageId == -1) {
			// We initiate editing an existing non-edit page.
			String oldUrl = request.getHeader("SENDING_CLIENT_URL");
			String oldPath = getPath(oldUrl);
			MultiValueMap<String, String> paramsSpringStyle = parseRequestParams(oldUrl);
			var params = convertSpringStyleMapToStandardMap(paramsSpringStyle);


			String[] oldPathElements = WebFrontendDispatchServlet.splitPathElements(oldPath);

			page = WebFrontendDispatchServlet.createPage(oldPathElements, params, userSession);  // TODO: Register the page only if necessary, that is if server-side page state is required.
			page.getBody().resetModificationMarkersRecursively();
			editPageId = userSession.registerEditorPage(page);



		} else {
			page = userSession.getEditorPage(editPageId);
		}


		if (page == null) {
			logAndReturnJsonErrorMsg("Ajax invokation with no known main page in UserSession. ", out);
			return;
		}


		//
		// Only one thread at a time can access a page
		//
		userSession.initializeAjaxProcessing();   // TODO REMOVE
		synchronized(page) {

			MsgAjaxResponse msgAjaxResponse;
			try {
				msgAjaxResponse = AjaxDispatcher.executeServletCommand(rootNode, userSession, page);    // CORE FUNCTIONALITY
				msgAjaxResponse.setEditPageId(editPageId);
			} catch (ErrorInSessionHandling ex) {
				out.println(JsonUtils.generateJson(new MsgErrorInfo("Session expired!")));
				return;
			}

			/*
			 *  Process the changed HTML
			 */
			ArrayList<Pair<HtmlTag, MsgClientUpdate>> htmlUpdates = page.collectPageUpdates();
			msgAjaxResponse.addHtmlUpdates(htmlUpdates);

			page.getBody().resetModificationMarkersRecursively();

			/*
			 * Generate output
			 */
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			String ajaxResponseJson = JsonUtils.generateJson(msgAjaxResponse);


			// JsonNode ajaxResponseTree= JsonUtils.generateJsonTree(msgAjaxResponse);

			out.println(ajaxResponseJson);
			userSession.stopAjaxProcessing();  // TODO REMOVE
			LOG.debug("Ajax response to be sent:\n" + ajaxResponseJson);

		}
		
		
	}
	


	public static String extractSendingPageName(JsonNode jsonNode) {
		JsonNode pageNameNode = jsonNode.get(AjaxDispatcher.ATTR_SENDING_PAGE_NAME);
		if (pageNameNode == null) {
			return null;
		}
		String sendingPageName = jsonNode.get(AjaxDispatcher.ATTR_SENDING_PAGE_NAME).asText();
		return sendingPageName;
	}


//	/**
//	 * Returns the value of the child of the given node with the name AjaxDispatcher.ATTR_EDIT_PAGE_ID
//	 * @param jsonNode Node whose children are searched
//	 * @return NO_EDIT_PAGE_ID_FOUND if no node found, value of this node otherwise.
//	 */
//	public static int extractEditPageId(JsonNode jsonNode) {
//		JsonNode node = jsonNode.get(AjaxDispatcher.ATTR_EDIT_PAGE_ID);
//		if (node == null) {
//			return NO_EDIT_PAGE_ID_FOUND;
//		}
//
//		String nodeText = node.asText();
//
//		int result = NO_EDIT_PAGE_ID_FOUND;
//		try {
//			result = Integer.parseInt(nodeText);
//		}
//		catch (NumberFormatException ex) {
//			throw new MalformedClientDataException(AjaxDispatcher.ATTR_EDIT_PAGE_ID + " is not an integer: " + nodeText);
//		}
//		return result;
//	}
	
	public static void logAndReturnJsonErrorMsg(String errorMessage, PrintWriter out) {
		LOG.error(errorMessage);
		out.println(JsonUtils.generateJson(new MsgErrorInfo(errorMessage)));
	}

	public void destroy() {
		// do nothing.
	}
}
