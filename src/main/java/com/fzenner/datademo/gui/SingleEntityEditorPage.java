package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.*;
import com.kewebsi.html.HtmlButtonStandard;
import com.kewebsi.html.HtmlPage;
import com.kewebsi.html.popup.HtmlModalDialog;
import com.kewebsi.html.search.EntityEditorState;
import com.kewebsi.html.search.EntityFieldDiv;
import com.kewebsi.html.table.ChildTableModel;
import com.kewebsi.html.table.HtmlPowerTable;
import com.kewebsi.service.StaticEntityService;
import com.kewebsi.util.CommonUtils;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;


public class SingleEntityEditorPage<T> extends HtmlPage implements CommandWithParamHandler {


	public final static String CREATE_NEW = "CREATE_NEW";

	public DtoAssistant<T> entityAssistant ;
	protected EntityEditorState<T> entityEditorState;

	public SingleEntityEditorPage(String pageName, UserSession userSession, Long entityId, DtoAssistant<T> dtoAssistant, LinkAssistant... linksForChildTables) {
		super(dtoAssistant.getEntityNamePlural(), pageName);
		this.userSession = userSession;
		this.entityAssistant = dtoAssistant;
		this.entityEditorState = new EntityEditorState<T>(dtoAssistant, "", linksForChildTables);


		if (entityId != null) {
			T entity = StaticEntityService.searchForEntity(dtoAssistant, entityId);
			if (entity != null) {
				dtoAssistant.mapEntityToPageState(entity, entityEditorState);
				populateChildTableStates();
			}
		}




		createHtmlObjects();

	}

	protected void populateChildTableStates() {
		Long parentObjectId = entityEditorState.getEntityId();
		for (ChildTableModel cts : entityEditorState.getChildrenTableStates()) {

			DtoAssistant parentDtoAssistant = entityAssistant;
			DtoAssistant childDtoAssistant = cts.getDtoAssistant();
			LinkAssistant linkAssistant = cts.getParentLinkAssistant();


			if (linkAssistant instanceof ManyToManyLinkAssistant) {
				ManyToManyLinkAssistant m2mla = (ManyToManyLinkAssistant) linkAssistant;
				String linkTableName = m2mla.getManyToManyTableName();
				String fkColNameToParent = m2mla.getFkColNameFor(parentDtoAssistant);
				String fkColNameToChild = m2mla.getFkColNameFor(childDtoAssistant);
				String childTableName = childDtoAssistant.getTableName();
				String childIdColName = childDtoAssistant.getIdAssistant().getDbColName();

				String joinClause = "INNER JOIN " + linkTableName + " as lt on lt." + fkColNameToChild + " = " + childTableName + "." + childIdColName + " and lt." + fkColNameToParent + " = " + parentObjectId;
				var resultList = StaticEntityService.searchForEntities(childDtoAssistant, joinClause, null);
				cts.setData(resultList);


			}
		}
	}
	
	public void createHtmlObjects() {

		//
		// Page elements
		//

		String idPrefix = entityAssistant.getSymbol() + "singleentity";

		var entityFieldDiv = new EntityFieldDiv<>(entityEditorState.getPageStateVars());

		//
		// New-Button
		//
		var buttonNew = new HtmlButtonStandard(idPrefix + "-New", "New") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				if (entityEditorState.pageVarsWereEdited()) {

					GuiAction executeWhenConfirmed = () -> {
						entityEditorState.clearPageVars();
						return MsgAjaxResponse.createSuccessMsg();
					};

					var modalDialog = new HtmlModalDialog("nochneid", "Ungesicherte Eingaben verwerfen?", "Ok", executeWhenConfirmed);
					SingleEntityEditorPage.this.getBody().addChild(modalDialog);
					var msg = MsgAjaxResponse.createSuccessMsg();
					msg.setHtmlTagToFocus(modalDialog.getId());
					return msg;

				} else {
					entityEditorState.clearPageVars();
					entityEditorState.setEntityId(null);
					return MsgAjaxResponse.createSuccessMsg();
				}
			}
		};


		//
		// Save-Button
		//
		var buttonSave = new HtmlButtonStandard(idPrefix + "-Save", "Save") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				return StandardController.insertOrUpdateEntity(entityAssistant, entityEditorState);
			}
		};



		//
		//Layout
		//
		body.addChild(buttonNew);
		body.addChild(buttonSave);
		body.addChild(entityFieldDiv);


		//
		// Child table
		//
		// Temporarily we assume that there is one child relation.
		Collection<ChildTableModel> childTableStates = entityEditorState.getChildrenTableStates();
		if (CommonUtils.hasData(childTableStates)) {
			ChildTableModel firstChildTableState = (ChildTableModel) childTableStates.stream().findFirst().get();
			var childrenAssistant = firstChildTableState.getDtoAssistant();
			var tableModel = firstChildTableState;
			// tableModel.setParentLinkAssistant(firstChildTableState.getLinkAssistant());
			HtmlPowerTable<?> table = new HtmlPowerTable<>(tableModel, "child"+childrenAssistant.getSymbol() + "childtable", PowerTableController.handlePowertableAction);


			var buttonNewChild = new HtmlButtonStandard(idPrefix + "-newchild", "New" + childrenAssistant.getEntityNameSingular()) {
				@Override
				public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
					var msgResponse =PowerTableController.createEntityForTableEditCore(table, 0);
					return msgResponse;
				}
			};

			body.addChild(buttonNewChild);


			body.addChild(table);
		}

	}


//	public static MsgAjaxResponse handleEventWithParams(JsonNode rootNode , UserSession userSession, HtmlPage page) {
//		MsgClientActionAndTag msg = JsonUtils.parseJsonFromClient(rootNode, MsgClientActionAndTag.class);
//		HtmlButtonStandard button = (HtmlButtonStandard) page.getDescendent(msg.tagId);
//
//		if (button == null) {
//			throw new MalformedClientDataException("Did not find button for tagId " + msg.tagId);
//		}
//
//		MsgAjaxResponse response = button.handleClick(rootNode , userSession);
//		return response;
//	}




	@Override
	public String getHtml() {
		return super.getHtml();
	}

	public DtoAssistant getEntityAssistant() {
		return entityAssistant;
	}

	public EntityEditorState getEntityEditorState() {
		return entityEditorState;
	}

	public String idPrefix() {
		return entityAssistant.getEntityNameSingular() + "-";
	}

	public EntityEditorState getPageState() {
		return entityEditorState;
	}

	@Override
	public MsgAjaxResponse handleCommandWithParam(String command, String param, UserSession userSession) {
		MsgAjaxResponse response = null;
		switch(command) {
			case CREATE_NEW -> {
				entityEditorState.clearPageVars();
				response = MsgAjaxResponse.createSuccessMsg();
			}
			default -> {
				throw new NotImplementedException("Command not known:" + command);
			}
		}
		return response;
	}
}


