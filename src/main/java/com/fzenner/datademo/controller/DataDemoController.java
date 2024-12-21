package com.fzenner.datademo.controller;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.StaticContextAccessor;
import com.fzenner.datademo.entity.ingredient.Ingredient;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.taco.Taco;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.fzenner.datademo.gui.DataDemoPage;
import com.fzenner.datademo.gui.DataDemoPageState;
import com.fzenner.datademo.service.DataDemoService;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgInvokServiceWithParams;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.GuiCallbackRegistrar;
import com.kewebsi.controller.StaticGuiDelegate;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.html.HtmlPage;
import com.kewebsi.util.JsonUtils;

public class DataDemoController {

	// public static GuiCallbackRegistrar guiCallbackRegistrar;// = AjaxDispatcher.getGuiCallbackRegistrar();
	
	/**
	 * We need to put the callback function into a variable in order to be able to
	 * check for equality. We cannot compare lambdas naively, they are never the
	 * same, unless they are in the same variable. Illustration:
	 * <p>
	 * <code> GuiCallbackFunction lambda1 = Controller::guiCallback;
	 * GuiCallbackFunction lambda2 = Controller::guiCallback;
	 * 
	 * if (!lambda1.equals(lambda2)) {
	 *    System.out.println("Same lambdas are not the same!"); 
	 * } 
	 * 
	 * 
	 * // Workaround 1: Put the lambda in a variable.
	 * GuiCallbackFunction lambda3 = lambda1; 
	 * if (lambda1 == lambda3) {
	 * 	   System.out.println("Workaround works!"); 
	 * } <code>
	 * 
	 
	 * Workaround 2: Wrap the lambda in an object that will be instantiated only
	 * once. That has the advantage, that we can pack more functionality
	 * in such a wrapper object. (ID definition, registering,...)
	 *  
	 
	 * We need that for reverse lookups to find the (HTML) id of a function that we
	 * want to link to an HTML element while providing a tight methodoloy to linke
	 * the HTML Element with the callback. More precisely, we want to be able to link
	 * an HTML Element to a callback by providing only the callback, without worrying about
	 * or providing a string-type ID for it. For example, we want to write (simplified)
	 * 
	 * var myButton = new CallbackButton("Hello World", myCallbackLambda);
	 * 
	 * and not have to write 
	 * 
	 * var myButton = new CallbackButton("Hello World", myCallbackLambda, "myCallbackLambdaId");
	 * 
	 * The ID is needed, so that when the HTTP request comes, we can find the right callback
	 * in principle as follows:
	 * 
	 * String callbackId = myHttpRequest.getCallbackId();
	 * CallbackFuntion lambda = someRegistrar.findCallbackForId(callbackId);
	 * lambda.execute(myHttpRequest.getRelevantParamters());
	 * 
	 * In other words, we need to retrieve a lambda from an string ID. 
	 * For the button constructor to be able to find an ID for a callback, the link between the 
	 * callback and its ID must be stored somewhere. And here is the catch: If create this linke
	 * at time point a by 
	 * 
	 * registar.registerCallback(MyController::myMethod, "myMethodId") 
	 * 
	 * and later link the callback to button as follows
	 * 
	 * var myCallback = registrar.findCallback(MyController::myMethod), then it will find no entry
	 * in the registrar, since the second mentioning of MyController::myMethod is not identical
	 * to the first.
	 * 
	 * So it is essential, that we put the callback into a variable and refer to it only via 
	 * referencing this variable.
	 * 
	 * It is NOT a sufficient reason to create a callback wrapper though.
	 * The wrapper encapsulates the link between ID and lambda in the wrapper object itself.
	 * If we do not use a wrapper, the button would need to access the registrar at design time.
	 * THe wrapper realizes the nice idea that a method has a nice string-id.
	 * Plus this wrapper handles the registration and definition/construction in one tight step.
	 * (This way, we can decouple object definition from the registrar completely. When we receive
	 * HTTP messages, we do not necessarily care about from which HTML object the message came,
	 * which means if in this phase, there is no link between object and registrar.
	 * 
	 * Consider 
	 * wrappedCallback = registrar.register((rootNode , userSession) -> {return controller.doSomething(rootNode, userSession), callBackStringId);
	 * wrappedCallback = registrar.register(Controller::doSomething, callBackStringId);
	 * 
	 * 
	 * Callbacks: Note: The following is not true in general
	 * MyClass::myCallBack.equals(MyClass::myCallBack) or MyClass::myCallBack ==
	 * MyClass::myCallBack hence we need to write this lambda expression only once.
	 * 
	 */
	



	
	//
	// Create generic powertable callbacks.
	//
	// public static final StaticGuiDelegate handlePowertableAction  = new StaticGuiDelegate(PowerTableController::handlePowertableAction, "handlePowertableAction", getGuiCallbackRegistrar());

	
	//
	// Taco specific callbacks
	//
	public static final StaticGuiDelegate insertTacoAndUpdatePage = new StaticGuiDelegate(DataDemoController::insertTacoAndUpdatePage, "insertTacoAndUpdatePage", getGuiCallbackRegistrar());
	public static final StaticGuiDelegate searchForTacos          = new StaticGuiDelegate(DataDemoController::searchForTacos, "searchForTacos", getGuiCallbackRegistrar());


	//
	// Ingredient specific callback
	//
	public static final StaticGuiDelegate searchForIngredients          = new StaticGuiDelegate(DataDemoController::searchForIngredients, "searchForIngredients", getGuiCallbackRegistrar());
	public static final StaticGuiDelegate insertIngredientAndUpdatePage = new StaticGuiDelegate(DataDemoController::insertIngredientAndUpdatePage, "insertIngredientAndUpdatePage", getGuiCallbackRegistrar());





	public static final StaticGuiDelegate setPingo = new StaticGuiDelegate(DataDemoController::setPingo, "setPingo", getGuiCallbackRegistrar());



//	static StaticGuiCallbackFunction lambda = (JsonNode rootNode , UserSession userSession) -> { int i=1; return new AjaxResponseMsg();};
//	public static final StaticGuiDelegate shortcutTest =  new StaticGuiDelegate(lambda , "insertIngredientAndUpdatePage", getGuiCallbackRegistrar());
//	public static final StaticGuiDelegate shortcutTest2 =  new StaticGuiDelegate((JsonNode rootNode , UserSession userSession) -> { int i=1; return new AjaxResponseMsg();} , "insertIngredientAndUpdatePage", getGuiCallbackRegistrar());
//	public static final DynamicGuiDelegate<TestController> eatMe = new DynamicGuiDelegate<>(new TestController("eatMe"), TestController::helloWorld, "testMe", getGuiCallbackRegistrar());
	
	
//	static TestController testController2 = new TestController("eatMe");
//	
//	public static final StaticGuiDelegate shortcutTest3 =  
//			new StaticGuiDelegate(
//					(rootNode , userSession) -> {return testController2.helloWorld(rootNode, userSession);},
//					"test3", 
//					getGuiCallbackRegistrar()    );
//	
//	
//	/**
//	 * Call this method in order to force the initialization of static fields, especially the instances of GuiDelegate.
//	 */
//	public static void initStaticFields() {
//		 DynamicGuiCallbackFunction<TestController> ccc = TestController::helloWorld2;
//		 DynamicGuiDelegate<TestController> del = new DynamicGuiDelegate<TestController>(ccc);
//		 
//		 DynamicGuiDelegate<TestController> del2 = new DynamicGuiDelegate<TestController>(TestController::helloWorld2);
//		 
//	}


	public static MsgAjaxResponse insertIngredientAndUpdatePage(JsonNode rootNode, UserSession userSession, HtmlPage page) {

		// Create and populate entity
		var htmlFieldDataNode   =  MsgInvokServiceWithParams.getHtmlFieldDataNode(rootNode);
		Ingredient entity = new Ingredient();
		IngredientAssistant assist = IngredientAssistant.getGlobalInstance();

		try {
			assist.mapDtoToEntity(htmlFieldDataNode, entity);
		} catch (ExpectedClientDataError ex) {
			return MsgAjaxResponse.createErrorMsg(ex.getMessage());
		}


		// Persist entity
		Ingredient persistedEntity = getDataDemoService().persist(entity);

		// Update Page State
		// var page = (DataDemoPage) userSession.getPage(DataDemoPage.PAGENAME);
		var state = (DataDemoPageState) page.getPageState();

		// var state = userSession.getOrCreateDataDemoPageAndState();
		state.setIngredientId(persistedEntity.getId());
		state.setIngredientName(persistedEntity.getName());
		state.setIngredientType(persistedEntity.getType());
		return MsgAjaxResponse.createSuccessMsg();
	}




	public static MsgAjaxResponse insertTacoAndUpdatePage(JsonNode rootNode, UserSession userSession, HtmlPage page) {
		var htmlFieldDataNode   = MsgInvokServiceWithParams.getHtmlFieldDataNode(rootNode);

		// Create and populate entity
		Taco newTaco = new Taco();
		TacoAssistant h = TacoAssistant.getGlobalInstance();

		try {
			h.mapDtoToEntity(htmlFieldDataNode, newTaco);
		} catch (ExpectedClientDataError ex) {
			return MsgAjaxResponse.createErrorMsg(ex.getMessage());
		}

		// Invoke Service
		DataDemoService dataDemoService = StaticContextAccessor.getBean(DataDemoService.class);
		Taco persistedTaco = dataDemoService.persistNewTaco(newTaco);

		// Update Page State
		DataDemoPageState state = (DataDemoPageState) page.getPageState();
		state.setTacoId(persistedTaco.getId());
		state.setTacoName(persistedTaco.getName());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = simpleDateFormat.format(persistedTaco.getCreatedAt());
		state.setTacoCreatedAt(formattedDate);
		return MsgAjaxResponse.createSuccessMsg();
	}



	private static void mapGuiInputToState(JsonNode htmlFieldDataNode, UserSession userSession, HtmlPage page) {
		DataDemoPageState state = (DataDemoPageState) page.getPageState();
		state.setTacoId(JsonUtils.getValueOfChildNode(htmlFieldDataNode,   TacoAssistant.TacoFields.tacoId));
		state.setTacoName(JsonUtils.getValueOfChildNode(htmlFieldDataNode, TacoAssistant.TacoFields.tacoName));
	}



	public static MsgAjaxResponse searchForTacos(JsonNode rootNode, UserSession userSession, HtmlPage page) {
		var htmlFieldDataNode   = MsgInvokServiceWithParams.getHtmlFieldDataNode(rootNode);
		mapGuiInputToState(htmlFieldDataNode, userSession, page);
		DataDemoService dataDemoService = StaticContextAccessor.getBean(DataDemoService.class);
		var tacos = dataDemoService.searchTacos();
		mapServiceResultToState(userSession, tacos, page);
		return MsgAjaxResponse.createSuccessMsg();
	}


	public static MsgAjaxResponse searchForIngredients(JsonNode rootNode, UserSession userSession, HtmlPage page) {
		DataDemoService dataDemoService = StaticContextAccessor.getBean(DataDemoService.class);
		var searchResult = dataDemoService.searchIngredient();
		DataDemoPageState state = (DataDemoPageState) page.getPageState();
		state.setIngredients(searchResult);
		return MsgAjaxResponse.createSuccessMsg();
	}

	private static void mapServiceResultToState(UserSession userSession, Iterable<Taco> tacos, HtmlPage page) {
		DataDemoPageState state = (DataDemoPageState) page.getPageState();
		state.setTacos(tacos);
	}

	
//	public static JsonNode getHtmlFieldDataNode(JsonNode rootNode) {
//		return rootNode.get(ServerMessageHandler.ATTR_HTML_FIELD_DATA);  
//	}


	public static DataDemoService getDataDemoService() {
		return StaticContextAccessor.getBean(DataDemoService.class);
	}


	public static GuiCallbackRegistrar getGuiCallbackRegistrar() {
		return GuiCallbackRegistrar.getInstance();
	}

	public static MsgAjaxResponse setPingo(JsonNode rootNode, UserSession userSession, HtmlPage htmlPage) {
		System.out.println("PINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGOPINGO");
		return MsgAjaxResponse.createSuccessMsg();
	}


//	public static DataDemoPageState getPageState(UserSession userSession) {
//		var page = (DataDemoPage) userSession.getPage(DataDemoPage.PAGENAME);
//		return (DataDemoPageState) page.getPageState();
//	}

	
}
