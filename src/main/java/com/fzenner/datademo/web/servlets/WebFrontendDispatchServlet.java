package com.fzenner.datademo.web.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fzenner.datademo.gui.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fzenner.datademo.entity.CustomerAssistant;
import com.fzenner.datademo.entity.ReservationAssistant;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.HtmlPage;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.DebugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.UserSessionHandler;
import com.kewebsi.util.SpringUtils;



/*
 * This servlet serves all content prefixed with 
 *     
 *     urlPatterns = {"/webFrontend/*"}
 *     
 *  (see Java annotation below), 
 *   
 * This comment gives some background information about the usage of URLs for HTML files (e.g. index.html),
 * script files (e.g. myscript.js) included by the HTML files and servlets, that read those HTML files as templates 
 * (because the replace some values in the HTML file). 
 * During development time, you want to be able to open the HTML (template) file locally by your browser and maintain
 * the functionality, that its included (javascript) files are correctly loaded by the browser. The include mechanism
 * must hence work in two situations: When you open the HTML (template) file locally and when the HTML (template) file
 * is loaded and served by an servlet. (Note that the included (javascript) file is not read by the template serving
 * servlet.)
 *   
 * In other words, template-servlets and script files should look as coming out of the same directory
 * from the point of view of the browser, otherwise local opening of the files will not work. 
 * 
 * Here are the details and and elaboring example
 * 
 * If the templates or HTML files served by a servlet include script files, which are not 
 * served by this servlet but via another URL, then the URL for those templates or HTML files should
 * have the same prefix (path) than this servlet.
 * 
 * Otherwise the HTML-include-mechanism <script src="scriptPathAndLocation"> works not in both situations when 
 * a) the root HTML file is opened directly from the file system by the browser
 * b) the root HTML file is opened as parsed template via the servlet.
 * 
 * Example: 
 * Assume you write a servlet mapped to 
 * 
 * http://myserver/myservlet 
 * 
 * That servlet generates a page (e.g. via filling a Html-template) which includes a file myScriptFile.js.

 * Assume you want to put myScriptFile.js in a subdirectory of the virtual root for web content in spring,
 * (You might want to do this because you do not like web content in the same directory as for example the
 * application.properties file.)
 * For example, you put your script file here:
 * 
 * <virtualRoot>/webFrontend/myScriptFile.js
 * 
 * which corresponds during coding to  
 * 
 * <localProjectRootFolder>/src/main/resources/webFrontend/myScriptFile.js
 * 
 * and after compilation to
 * 
 * <localProjectRootFolder>/bin/main/webFrontend/myScriptFile.js
 * 
 * Assume you have a template file myTemplate.html served out by your servlet in the same directory (directories) as
 * myScriptFile.js. 
 * 
 * When you include the script file in the HTML file as follows:
 * 
 * <script type="text/javascript" src="./myScriptFile.js" charset="utf-8"></script>
 * 
 * Then this will open fine when you open myTemplate.html directly from the file system in your browser.
 * (The browser looks in the same directory as the including file.)
 * 
 *  But when the template is opened via the servlet, the included script will not be found. Since the browser
 *  will look for http://myserver/myScriptFile.js while it should look for http://myserver/webFrontend/myScriptFile.js
 *  
 *  THe solution is to map the servlet under the same subdirectory, in our example under /webFrontend/.
 * 
 */
@WebServlet(
		  name = "AnnotationExample",
		  description = "Example Servlet Using Annotations",
		  urlPatterns = {"/webFrontend/*"}
		)
public class WebFrontendDispatchServlet extends HttpServlet {


	private static final long serialVersionUID = -770426353828524364L;
	private static Logger LOG = LoggerFactory.getLogger(WebFrontendDispatchServlet.class);

	private String message;

	@Autowired
	UserSessionHandler userSessionHandler;
	
	public static final String TACO_EDITOR_PAGENAME = "TacoEditor";
	public static final String INGREDIENT_EDITOR_PAGENAME = "IngredientEditor";

	public static final String SINGLE_INGREDIENT_EDITOR_PAGENAME = "SingleIngredientEditor";

	public static final String SINGLE_TACO_EDITOR_PAGENAME = "SingleTacoEditor";


	/**
	 * Part of the URI that indicates that files (including templates) in the web frontend are being requested.
	 * This value should always match the urlPattern of this servlet except for the wildcard at the end of the 
	 * URL pattern. 
	 */
	public static final String WEB_FRONTEND_URI_PREFIX = "/webFrontend/";
	
	/**
	 * The folder in which we look for web frontend files (including templates). Slashes will be added by the functions
	 * that use this value, so you do not have to worry.
	 */
	public static final String WEB_FRONTEND_CLASSPATH_SUBFOLDER = "webFrontend";
	
	
	

	public void init() throws ServletException {
		// Do required initialization
		message = "Hello World";
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// getRequestURI does return only the path of the url (see https://de.wikipedia.org/wiki/Uniform_Resource_Identifier
		// For example, when the request was "http://localhost:8080/webFrontend/TacoEditor/showme?id=5" it returns "/webFrontend/TacoEditor/showme"
		String uri = request.getRequestURI();

		String[] pathElements = splitPathElements(uri);

		if (pathElements[0].equals(WEB_FRONTEND_URI_PREFIX)) {
			throw new MalformedClientDataException(String.format("Expected first path element is:%s. But received is:%s", WEB_FRONTEND_URI_PREFIX, pathElements[0]));
		}

		String pathIdx1 = pathElements[1];


		String postfix = getPostFix(uri, WEB_FRONTEND_URI_PREFIX);

		var parameterMap = request.getParameterMap();
		
		boolean serveFlatFile = false;

		boolean pageServed = false;
		if (postfix.equals("index") || postfix.length()== 0) {
			//
			// Start-page / default page.
			//
			response.setContentType("text/html");
			response.addHeader("Access-Control-Allow-Origin", "*");  // DANGEROUS, REMOVE FOR PRODUCTION
			return;
		} else if (postfix.equals("resetSession")) {
			HttpSession session = request.getSession();
			String sessionId = session.getId();
			response.getWriter().println("Session " + sessionId + " invalidated");
			request.getSession().invalidate();
			return;
		} else {
			// *** MAIN CALL *** //
			pageServed = createOrServeInitialPage(pathElements, request, response);
			if (pageServed) {
				return;
			}
		}

		if (postfix.endsWith(".css")) {
			response.setContentType("text/css");
			serveFlatFile = true;
		} else if (postfix.endsWith(".js")) {
			response.setContentType("text/javascript");
			serveFlatFile = true;
		} else if (isJavaScriptFileWithoutEnding(postfix)) {
			postfix = postfix + ".js";
			response.setContentType("text/javascript");
			serveFlatFile = true;
		} else if (postfix.endsWith("getGuiState")) {
			response.setContentType("text/css");
			response.addHeader("Access-Control-Allow-Origin", "*");  // DANGEROUS, REMOVE FOR PRODUCTION
			response.getWriter().println("{ 'This is myGuiState !!!!'}\n");
		}

		else {
			response.setContentType("text/html");
			serveFlatFile = true;
		}
		
		if (serveFlatFile) {
			// Serve the file.
			ServletOutputStream out = response.getOutputStream();
			String pathPostFix = "/" + WEB_FRONTEND_CLASSPATH_SUBFOLDER + "/" + postfix;
			SpringUtils.readClassPathResourceFileToOutputStream(pathPostFix, out);
		}
	}


	public boolean isJavaScriptFileWithoutEnding(String path) {

		if (path.contains("/js/") || path.contains("/jsFromTs")) {
			if (! path.endsWith(".js") && ! path.endsWith(".map")) {
				return true;
			}
		}
		return false;
	}


	public boolean createOrServeInitialPage(String[] pathElements, HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserSession userSession = userSessionHandler.getUserSession(request);

		var paramMap = request.getParameterMap();

		String pageName = pathElements[pathElements.length-1];

		// *** MAIN CALL *** //
		HtmlPage page = createPage(pathElements, paramMap, userSession);

		if (page != null) {
			response.setContentType("text/html");
			String htmlOut = page.getHtml();
			LOG.debug(htmlOut);
			PrintWriter out = response.getWriter();
			out.println(htmlOut);
			page.getBody().resetModificationMarkersRecursively();
			return true;
		}
		return false;
	}


	public static HtmlPage createPage(String[] pathElements, Map<String, String[]> paramMap, UserSession userSession) {
		if (pathElements.length == 0) {
			return null;
		}
		String pageName = pathElements[pathElements.length - 1];  // We do not distinguish modules for now.

		Long id = getParamValAsLong("id", paramMap);
		HtmlPage result = switch (pageName) {
			case SINGLE_TACO_EDITOR_PAGENAME -> {
				TacoAssistant ta = TacoAssistant.getGlobalInstance();
				HtmlPage newPage =  new SingleEntityEditorPage(SINGLE_TACO_EDITOR_PAGENAME, userSession, id, ta, ta.getManyToManyLinkAssistantTo(IngredientAssistant.getGlobalInstance()));
				yield newPage;
			}
			case SINGLE_INGREDIENT_EDITOR_PAGENAME -> new SingleEntityEditorPage(SINGLE_INGREDIENT_EDITOR_PAGENAME, userSession, id, IngredientAssistant.getGlobalInstance());
			case TACO_EDITOR_PAGENAME -> new EntityNavigatorPage(TACO_EDITOR_PAGENAME, userSession, id, TacoAssistant.getGlobalInstance());
			case INGREDIENT_EDITOR_PAGENAME -> new EntityNavigatorPage(INGREDIENT_EDITOR_PAGENAME, userSession, id, IngredientAssistant.getGlobalInstance());
			case "SingleCustomerEditor" -> new SingleEntityEditorPage("SingleCustomerEditor", userSession, id, CustomerAssistant.getGlobalInstance());
			case "SingleReservationEditor" -> new SingleEntityEditorPage("SingleReservationEditor", userSession, id, ReservationAssistant.getGlobalInstance());
			case "CustomerNavigator" -> new EntityNavigatorPage("CustomerNavigator", userSession, id, CustomerAssistant.getGlobalInstance());
			case "ReservationNavigator" -> new EntityNavigatorPage("ReservationNavigator", userSession, id, ReservationAssistant.getGlobalInstance());
			case "DateTester" -> new DateTesterPage();
			case "IntegerFieldRequiredTestPage" -> new IntegerFieldTestPage("IntegerFieldTestPage", "IntegerFieldTestPage", true);
			case "IntegerFieldRequiredDisableTestPage" -> new IntegerFieldTestPageDisable("IntegerFieldRequiredDisableTestPage", "IntegerFieldRequiredDisableTestPage", true);
			case "IntegerFieldOptionalDisableTestPage" -> new IntegerFieldTestPageDisable("IntegerFieldOptionalDisableTestPage", "IntegerFieldOptionalDisableTestPage", false);
			case "MirroredIntegerFieldTestPageDisable" -> new MirroredIntegerFieldTestPageDisable("IntegerFieldTestPageDisable", "IntegerFieldTestPageDisable");

			case "Navigator" -> new NavigatorPage(1, userSession);
			default -> null;

		};

		if (DebugUtils.DEBUG_CHECKS_ON) {
			if (result != null) {
				LOG.debug("XXXXX PAGE CREATED XXXXX");
			}
		}

		return result;
	}


	public static String getParamVal(String name, Map<String, String[]> paramMap) {
		String[] paramStrArr = paramMap.get(name);
		String val = null;
		if (paramStrArr != null && paramStrArr.length>0) {
			val = paramStrArr[0];
		}
		return val;
	}

	public static Long getParamValAsLong(String name, Map<String, String[]> paramMap) {
		String idStr = getParamVal("id", paramMap);
		Long idLong = null;
		if (idStr != null) {
			try {
				idLong = Long.parseLong(idStr);
			}
			catch (NumberFormatException e) {
				idLong = null;
			}
		}
		return idLong;
	}
	
	/**
	 * Returns the "second part" of the input when cutting away the "first part".
	 * @param input String to get the postfix from.
	 * @param prefixRegexpPattern First part as regexp pattern 
	 * @return The postfix.
	 */
	public String getPostFix(String input, String prefixRegexpPattern) {

		String pattern = prefixRegexpPattern + "(.*)"; // "(.*)(\\d+)(.*)";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(input);

		String result = null;

		if (m.find()) {
			result = m.group(1);
		}

		return result;

	}


	/**
	 * Expects at input the path of an URL.
	 * Example: If the URL is
	 * http://localhost:8080/webFrontend/TacoEditor/showme?id=5
	 * then the path is
	 * /webFrontend/TacoEditor/showme
	 *
	 * Returns the strings of the path that are separated by slash ("/").
	 * Example: If the path is
	 * /webFrontend/TacoEditor/showme
	 * then the result is the array containing the three strings
	 * ["webFrontend", "TacoEditor", "showme"].
	 * This functions works independent from leading or trailing slashes ("/"). They will be ignored.
	 * @param uri THe path of an URL as explained above. See also HttpServiceRequest.getRequestURI()
	 * @return An array with the path elements as explained above.
	 */
	public static String[] splitPathElements(String uri) {
		if (CommonUtils.hasInfo(uri)) {
			boolean firstCharIsSlash = uri.charAt(0) == '/' ? true : false;
			boolean lastCharIsSlash = uri.charAt(uri.length() - 1) == '/' ? true : false;

			String strippedPath = uri;
			if (firstCharIsSlash) {
				strippedPath = uri.substring(1, uri.length());
			}

			if (lastCharIsSlash) {
				strippedPath = strippedPath.substring(0, strippedPath.length() - 1);
			}
			String[] result = strippedPath.split("/");
			return result;
		} else {
			return new String[0];
		}
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + "Post " +message + "</h1>");
	}

	
	public void destroy() {
		// do nothing.
	}
}