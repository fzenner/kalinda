package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.*;
import com.kewebsi.html.*;
import com.kewebsi.html.search.*;
import com.kewebsi.html.table.*;


public class  EntityNavigatorPage<T> extends HtmlPage {

	public DtoAssistant<T> entityAssistant ;
	protected EntityNavigatorState entityNavigatorState;

	public EntityNavigatorPage(String pageName, UserSession userSession, Long entityId, DtoAssistant<T> dtoAssistant) {
		super(dtoAssistant.getEntityNamePlural(), pageName);
		this.userSession = userSession;
		this.entityAssistant = dtoAssistant;
		this.entityNavigatorState = new EntityNavigatorState(dtoAssistant, "navigator");


//		if (entityId != null) {
//			T entity = EntityService.searchForEntity(entityAssistant, entityId);
//			dtoAssistant.mapEntityToPageState(entity, entityNavigatorState);
//		}



		// userSession.registerPage(this);
		createHtmlObjects();

	}
	
	
	public void createHtmlObjects() {

		//
		// Page elements
		//
		String idPrefix = entityNavigatorState.getPageStateId() + "-";

		var buttonTest = new HtmlButtonStandard("myTest", "Test It") {
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				return StandardController.makeATest();
			}


		};


		var searchDiv = new HtmlSearchDiv(entityAssistant, entityNavigatorState, entityAssistant.getEntityNameSingular()+ "-search");
		// var htmlButtonSearch = new HtmlButtonServerAction(idPrefix + "searchbutton", "Search", SearchDivController.executeEntityEditorSearch);
		var htmlButtonSearch = new HtmlButtonStandard(idPrefix + "searchbutton", "Search") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				return SearchDivController.executeEntityEditorSearch(rootNode, userSession, getPage());
			}
		};




		PowerTableModel<T> model = entityNavigatorState.getTableModel();
		HtmlPowerTable<T> table = new HtmlPowerTable<>(model, idPrefix + "resulttable", PowerTableController.handlePowertableAction);
		var htmlButtonSaveTacosOld = new HtmlButtonSaveTableData(idPrefix +"savetablebutton", "Save Table", table);

		var htmlButtonSaveTacos = new HtmlButtonStandard(idPrefix +"savetablebutton", "Save Table") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				return PowerTableController.saveTableData(table);
			}
		};



		var htmlDeleteTableEntry = new HtmlButtonStandard(idPrefix + "deletebutton", "Delete selected elements") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				return PowerTableController.openModalDeleteSelectedRows(table);
			}
		};


		var htmlButtonInsertEntityForTableEdit = new HtmlButtonStandard(idPrefix + "insertbutton", "New Element") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {

				int insertIdx = 0;
				int lastSelectedRow = model.getLastSelectedRowIdx();
				if (lastSelectedRow <= 0) {
					insertIdx =0;
				} else {
					insertIdx = lastSelectedRow +1;
				}

				return PowerTableController.createEntityForTableEditCore(table, insertIdx);
			}
		};


		//
		//Layout
		//
		var twoColumnDiv = new HtmlDiv2("twocol").addCssClass("horizontalDivFlow");
		var leftColDiv = new HtmlDiv2("leftcol");
		var rightColDiv = new HtmlDiv2("rightcol").addCssClass("rightColDiv");

		body.addChild(twoColumnDiv);
		twoColumnDiv.addChildren(leftColDiv,rightColDiv);

		leftColDiv.addChild(buttonTest);
		// XXX3 leftColDiv.addChild(buttonSave);
		// XXX3 leftColDiv.addChild(entityFieldDiv);  // XXXXXXXXXXXXX
		leftColDiv.addChild(searchDiv);	      // XXXXXXXXXXXXX
		leftColDiv.addChild(htmlButtonSearch);
		leftColDiv.addChild(htmlButtonInsertEntityForTableEdit);
		leftColDiv.addChild(htmlButtonSaveTacos);
		leftColDiv.addChild(htmlDeleteTableEntry);

		leftColDiv.addChild(table);

		var buttonDummy = new HtmlButtonStandard(idPrefix+"dummy", "Dummy") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				System.out.println("This is just a love song.");
				return MsgAjaxResponse.createSuccessMsg();
			}
		};

		rightColDiv.addChild(buttonDummy);

		EntityForDetailDisplayProvider entityProvider = new EntityForDetailDisplayProvider() {
			@Override
			public ManagedEntity<T> getManagedEntityForDetailDisplay() {
				var entity =  model.getManagedEntityForDetailDisplay();
				if (entity != null) {
					return entity;
				} else {
					return null;
				}
			}
		};


		LinkedEntityEditorState lees = new LinkedEntityEditorState(entityProvider, entityAssistant, entityNavigatorState.getPageStateId() + "-detail");

		EntityFieldDiv efd = new EntityFieldDiv(lees.getPageStateVars());
		rightColDiv.addChild(efd);

	}


	public DtoAssistant getEntityAssistant() {
		return entityAssistant;
	}

	public EntityNavigatorState getEntityEditorState() {
		return entityNavigatorState;
	}

	// @Override
	public EntityNavigatorState getPageState() {
		return entityNavigatorState;
	}




}


