package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.StaticContextAccessor;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgClientActionAndTag;
import com.fzenner.datademo.web.inmsg.MsgDateTimeEntered;
import com.fzenner.datademo.web.inmsg.MsgFieldDataEntered;
import com.fzenner.datademo.web.inmsg.MsgRadioButtonClicked;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.*;
import com.kewebsi.html.search.AtomicSearchConstraint;
import com.kewebsi.html.search.EntityEditorState;
import com.kewebsi.service.EntityEditorTransactionService;
import com.kewebsi.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

@Service
public class StandardController {

	public static final StaticGuiDelegate removeTag  = new StaticGuiDelegate(StandardController::removeTag, "removeTag", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate SMH_escapeKeyPressed = new StaticGuiDelegate(StandardController::SMH_escapeKeyPressed, "SMH_escapeKeyPressed", GuiCallbackRegistrar.getInstance());

	// public static final StaticGuiDelegate setPageStateVarVal = new StaticGuiDelegate(StandardController::setPageStateVarVal, "setPageStateVarVal", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate SMH_fieldDataEntered = new StaticGuiDelegate(StandardController::SMH_fieldDataEntered, "SMH_fieldDataEntered", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate SMH_dateTimeEntered = new StaticGuiDelegate(StandardController::SMH_dateTimeEntered, "SMH_dateTimeEntered", GuiCallbackRegistrar.getInstance());

	public static final StaticGuiDelegate handleInputChanged = new StaticGuiDelegate(StandardController::handleInputChanged, "handleInputChanged", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate handleRadioButtonClick = new StaticGuiDelegate(StandardController::handleRadioButtonClick, "handleRadioButtonClick", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate SMH_selectionMade = new StaticGuiDelegate(StandardController::SMH_selectionMade, "SMH_selectionMade", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate SMH_standardButtonClicked = new StaticGuiDelegate(StandardController::SMH_standardButtonClicked, "SMH_standardButtonClicked", GuiCallbackRegistrar.getInstance());
	// public static final StaticGuiDelegate handleFocusEvent = new StaticGuiDelegate(StandardController::handleFocusEvent, "handleFocusEvent", GuiCallbackRegistrar.getInstance());

	public static final StaticGuiDelegate SMH_checkBoxClicked = new StaticGuiDelegate(StandardController::SMH_checkBoxClicked, "SMH_checkBoxClicked", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate SMH_radioButtonClicked = new StaticGuiDelegate(StandardController::SMH_radioButtonClicked, "SMH_radioButtonClicked", GuiCallbackRegistrar.getInstance());

	public static final StaticGuiDelegate SMH_handleGuiEvent = new StaticGuiDelegate(StandardController::SMH_handleCustomGuiEvent, "SMH_handleGuiEvent", GuiCallbackRegistrar.getInstance());


	static final Logger LOG = LoggerFactory.getLogger(StandardController.class);


	public static <S extends PageState, T> MsgAjaxResponse removeTag(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		
		MsgClientActionAndTag msg = JsonUtils.parseJsonFromClient(rootNode, MsgClientActionAndTag.class);
		@SuppressWarnings("unchecked")
		HtmlTag tag = page.getElementById(msg.tagId);
		tag.remove();
		return MsgAjaxResponse.createSuccessMsg();
		
	}

	public static <S extends PageState, T> MsgAjaxResponse SMH_escapeKeyPressed(JsonNode rootNode , UserSession userSession, HtmlPage page) {

//		MsgInvokeServiceWithSimpleParams msg = JsonUtils.parseJsonFromClient(rootNode, MsgInvokeServiceWithSimpleParams.class);
//		HtmlTag tag = page.getElementById(msg.p1);
//		tag.remove();   umstellen auf tag id und MsgClientActionAndTag
//		return MsgAjaxResponse.createSuccessMsg();


		MsgClientActionAndTag msg = JsonUtils.parseJsonFromClient(rootNode, MsgClientActionAndTag.class);
		EscapeKeyHandler tag = (EscapeKeyHandler) page.getElementById(msg.tagId);
		if (tag==null) {
			System.out.println("OGOTTGOTOOTOTOTOTOTOTOTOTFO"); // XXXXXXXXXXXXXXXXXXX
		}
		tag.handleEscapeKeyPressed(userSession);
		return MsgAjaxResponse.createSuccessMsg();

	}


	public static <S extends PageState, T> MsgAjaxResponse SMH_fieldDataEntered(JsonNode rootNode , UserSession userSession, HtmlPage page) {  // TODO: Rename to setPageStateVar

		MsgFieldDataEntered msg = JsonUtils.parseJsonFromClient(rootNode, MsgFieldDataEntered.class);

		if (msg.tagId == null) {
			LOG.trace("XXXXXXXXXXXXXXX111ooo");  // Just for debugging
		}

		var changedTag = (InputChangeHandler) page.getDescendent(msg.tagId);
		changedTag.inputChanged(msg, rootNode, userSession);

		return MsgAjaxResponse.createSuccessMsg();
	}

	public static <S extends PageState, T> MsgAjaxResponse SMH_dateTimeEntered(JsonNode rootNode , UserSession userSession, HtmlPage page) {  // TODO: Rename to setPageStateVar

		MsgDateTimeEntered msg = JsonUtils.parseJsonFromClient(rootNode, MsgDateTimeEntered.class);

		if (msg.tagId == null) {
			LOG.trace("XXXXXXXXXXXXXXX111ooo");  // Just for debugging
		}

		var changedTag = (GuiObjChangeHandler) page.getDescendent(msg.tagId);
		changedTag.inputChanged(rootNode, userSession);

		return MsgAjaxResponse.createSuccessMsg();
	}

	public static <S extends PageState, T> MsgAjaxResponse SMH_childButtonClicked(JsonNode rootNode , UserSession userSession, HtmlPage page) {  // TODO: Rename to setPageStateVar

		MsgFieldDataEntered msg = JsonUtils.parseJsonFromClient(rootNode, MsgFieldDataEntered.class);

		var changedTag = (InputChangeHandler) page.getDescendent(msg.tagId);
		changedTag.inputChanged(msg, rootNode, userSession);

		return MsgAjaxResponse.createSuccessMsg();
	}


	public static <S extends PageState, T> MsgAjaxResponse handleRadioButtonClick(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		MsgClientActionAndTag msg = JsonUtils.parseJsonFromClient(rootNode, MsgClientActionAndTag.class);
		HtmlRadioButton changedTag = (HtmlRadioButton) page.getDescendent(msg.tagId);
		changedTag.setNonNullValueValidating(changedTag.getValueIfSelected().toString());
		return MsgAjaxResponse.createSuccessMsg();
	}


	public static MsgAjaxResponse SMH_standardButtonClicked(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		MsgClientActionAndTag msg = JsonUtils.parseJsonFromClient(rootNode, MsgClientActionAndTag.class);
		ButtonClickHandler button = (ButtonClickHandler) page.getDescendent(msg.tagId);

		if (button == null) {
			throw new MalformedClientDataException("Did not find button for tagId " + msg.tagId);
		}

		MsgAjaxResponse response = button.handleClick(rootNode , userSession);
		return response;
	}


	public static MsgAjaxResponse handleInputChanged(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		return SMH_fieldDataEntered(rootNode, userSession, page);
	}

	public static MsgAjaxResponse childButtonClicked(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		return SMH_childButtonClicked(rootNode, userSession, page);
	}



	public static MsgAjaxResponse SMH_selectionMade(JsonNode rootNode , UserSession userSession, HtmlPage page) {


		MsgFieldDataEntered msg = JsonUtils.parseJsonFromClient(rootNode, MsgFieldDataEntered.class);
		var selectBox = (SelectionMadeHandler )  page.getDescendent(msg.tagId);
		if (selectBox == null) {
			throw new MalformedClientDataException("Did not find button for tagId " + msg.tagId);
		}
		return selectBox.handleSelectionEvent(msg.value, rootNode, userSession);
	}

	public static MsgAjaxResponse SMH_checkBoxClicked(JsonNode rootNode, UserSession userSession, HtmlPage page) {

		MsgFieldDataEntered msg = JsonUtils.parseJsonFromClient(rootNode, MsgFieldDataEntered.class);



		var checkBox = (CheckBoxClickHandler) page.getElementById(msg.tagId);
		var tf = msg.value.toLowerCase();
		var checked = false;
		if (tf.startsWith("t") || tf.startsWith("y")) {
			checked = true;
		}

		return checkBox.handleClick(checked, rootNode, userSession);
	}


	public static MsgAjaxResponse SMH_radioButtonClicked(JsonNode rootNode, UserSession userSession, HtmlPage page) {
		MsgRadioButtonClicked msg = JsonUtils.parseJsonFromClient(rootNode, MsgRadioButtonClicked.class);
		HtmlRadioButton2 radioButton = (HtmlRadioButton2) page.getElementById(msg.tagId);


		AtomicSearchConstraint asc = radioButton.getAtomicSearchConstraint();
		asc.setConstraintTypeByEnumClearErrors(radioButton.getValueIfSelected());
		asc.setConstraintIsActive(true);

		radioButton.getButtonGroup().doNotUpdate();

		return MsgAjaxResponse.createSuccessMsg();
	}

	public static MsgAjaxResponse SMH_handleCustomGuiEvent(JsonNode rootNode, UserSession userSession, HtmlPage page) {
		String idOfTargetGuiElement = rootNode.get("tagId").textValue();
		HtmlTag target = page.getElementById(idOfTargetGuiElement);
		CustomGuiEventHandler customGuiEventHandler = (CustomGuiEventHandler) target;
		var msgOut = customGuiEventHandler.handleCustomGuiEvent(rootNode,userSession);
		return msgOut;
	}




	// @Transactional
	public static MsgAjaxResponse makeATest() {

		EntityManager em = StaticContextAccessor.getEntityManager();
		Query query = em.createNativeQuery("select * from Taco t");
		List list = query.getResultList();

		for(Object taco : list){
			System.out.println("Taco:" + taco);
		}

		return MsgAjaxResponse.createSuccessMsg();
	}


	// @Transactional
//	public static MsgAjaxResponse makeATest2() {
//
//		EntityManager em = StaticContextAccessor.getEntityManager();
//		Query query = em.createQuery("select t from Taco t");
//		List<Taco> list = query.getResultList();
//
//		for(Taco taco : list){
//			System.out.println("Taco:" + taco.getName());
//		}
//
//		return MsgAjaxResponse.createSuccessMsg();
//	}

	public static <T> MsgAjaxResponse insertOrUpdateEntity(DtoAssistant<T> entityAssistant, EntityEditorState<T> entityEditorState) {
		EntityEditorTransactionService transactionService = StaticContextAccessor.getBean(EntityEditorTransactionService.class);

		try {
			return transactionService.insertOrUpdateEntity(entityAssistant, entityEditorState);
		} catch (ExpectedClientDataError error) {
			return MsgAjaxResponse.createErrorMsg(error.getMessage());
		}
	}

	/**
	 * This method does three things:
	 * 1. Read the entity from DB.
	 * 2. Map the client data to the entity,
	 * 3. Save the entity to the db.
	 *
	 * Expects that the ID of the entity is set. (The entity should exist in the DB already.)
	 * The entity is read from the DB. This way, also attributes of the entity that are never written by a client
	 * are populated in memory. That means, the entity is fully populated before saved to the DB while avoiding to write
	 * potentially falsified data from the client to fields that should never be populated by the client (e.g.
	 * created_by, created_at,...)
	 *
	 * What is not done: After saving the entity to the DB, we do not read again the entity before mapping the data
	 * back to the state. So if the DB itself updates the data during the DB-update, it is not reflected in the
	 * in-memory-entity.
	 * @param entityAssistant
	 * @param entityEditorState
	 * @return
	 * @param <T>
	 */
//	@Transactional
//	public static <T> MsgAjaxResponse updateEntityXXX(DtoAssistant<T> entityAssistant, EntityEditorState entityEditorState) {
//		T entity = StaticEntityService.searchForEntity(entityAssistant,  entityEditorState.getEntityId());
//		MsgAjaxResponse errorMsg = null;
//		try {
//			entityAssistant.mapPageStateToEntity(entityEditorState, entity);
//		} catch (ExpectedClientDataError ex) {
//			errorMsg = MsgAjaxResponse.createErrorMsg(ex.getMessage());
//		}
//		MsgAjaxResponse result;
//		boolean dataShouldBeSaved = (errorMsg == null && ! entityEditorState.hasError());
//		if (dataShouldBeSaved) {
//			DataDemoService dataDemoService = StaticContextAccessor.getBean(DataDemoService.class);
//			dataDemoService.saveEntity(entity);
//			entityAssistant.mapEntityToPageState(entity, entityEditorState);
//			result = MsgAjaxResponse.createSuccessMsg();
//		} else {
//			if (errorMsg != null) {
//				result = errorMsg;
//			} else {
//				result = MsgAjaxResponse.createErrorMsg(entityEditorState.getError().getErrorMsg());
//			}
//		}
//		return result;
//	}

//	public static MsgAjaxResponse insertEntityXXX(DtoAssistant entityAssistant, EntityEditorState entityEditorState) {
//		Object entity = entityAssistant.createEntity();
//		MsgAjaxResponse errorMsg = null;
//		try {
//			// Does not necessarily map all fields. E.g. "created_at". So this can not be falsified by the client.
//			entityAssistant.mapPageStateToEntity(entityEditorState, entity);
//		} catch (ExpectedClientDataError ex) {
//			errorMsg = MsgAjaxResponse.createErrorMsg(ex.getMessage());
//		}
//
//		MsgAjaxResponse result;
//		boolean dataShouldBeSaved = (errorMsg == null && ! entityEditorState.hasError());
//		if (dataShouldBeSaved) {
//			DataDemoService dataDemoService = StaticContextAccessor.getBean(DataDemoService.class);
//			dataDemoService.saveEntity(entity);
//
//
//			entityAssistant.mapEntityToPageState(entity, entityEditorState);
//			result = MsgAjaxResponse.createSuccessMsg();
//		} else {
//			if (errorMsg != null) {
//				result = errorMsg;
//			} else {
//				result = MsgAjaxResponse.createErrorMsg(entityEditorState.getError().getErrorMsg());
//			}
//		}
//
//		return result;
//	}

}
